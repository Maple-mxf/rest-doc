package restdoc.client.restweb.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import restdoc.rpc.client.common.model.FieldType;
import restdoc.rpc.client.common.model.http.HttpApiDescriptor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The EndpointsListener provided report client api list info to server
 * <p>
 * Springboot应用启动需要做的任务有
 * 1 如果console端已经同步过了当前应用的API信息之后，则对已经存在的API文档进行记录建议开发者进行补充文档
 * 2 如果{@link restdoc.client.api.ServerProperties} 的service属性为空，则将当前应用的service补充维应用的ContextPath(去掉第一个"/")
 * 3 当透析一个API时，为每个API文档生成一份模板数据(测试用例) 仅为模板数据，并不准确
 * 4 导入的API的唯一ID为(controller+function).hashcode().toString()
 * 5 开发者可以将console端已有的API文档和应用内部已有的API可以做关联(关联关系可以理解为所属)
 * 6 所有的API文档具有关联性(API之间具有依赖性)
 * 7 如果部分API文档是通过应用生成的 则console端则应用增加标记图标，加强开发辨识度
 * 8 应用SDK代码全部更改为java 去掉kotlin
 *
 * @author Maple
 * @since 1.0.RELEASE
 */
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger log = LoggerFactory.getLogger(EndpointsListener.class);

    private List<HttpApiDescriptor> httpApiList;

    private final Environment environment;

    private final ObjectMapper mapper = new ObjectMapper();

    public EndpointsListener(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = event.getApplicationContext()
                .getBean(RequestMappingHandlerMapping.class)
                .getHandlerMethods();

        // Get global contextPath
        String contextPath = environment.getProperty("server.servlet.context-path", "");

        // Must be flatmap handler method
        // because of The one handler method has many request pattern

        // final report the emptyApiTemplates to remoting server
        this.httpApiList = handlerMethods.entrySet()
                .stream()
                .flatMap(hm -> {
                    RequestMappingInfo requestMappingInfo = hm.getKey();
                    HandlerMethod handlerMethod = hm.getValue();

                    // Filter view handler method
                    if (ResolverUtil.isViewHandler(handlerMethod)) return Stream.empty();

                    return requestMappingInfo.getPatternsCondition()
                            .getPatterns()
                            .stream()
                            // Filter
                            .filter(pattern -> !"/error".equals(pattern))
                            .map(pattern -> String.join("", contextPath, pattern))
                            .map(pattern -> {

                                HttpApiDescriptor emptyTemplate = new HttpApiDescriptor();
                                emptyTemplate.setName(requestMappingInfo.getName());

                                Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();
                                if (methods.isEmpty()) emptyTemplate.setMethod(RequestMethod.POST.name());
                                else if (methods.size() == 1)
                                    emptyTemplate.setMethod((new ArrayList<>(methods)).get(0).name());
                                else if (methods.contains(RequestMethod.GET)) emptyTemplate.setMethod(methods.stream()
                                        .max(Enum::compareTo)
                                        .map(Enum::name)
                                        .orElse(RequestMethod.POST.name()));

                                emptyTemplate.setPackageName(handlerMethod.getBeanType().getPackage().getName());
                                emptyTemplate.setEndpoint(handlerMethod.getMethod().getName());
                                emptyTemplate.setPattern(pattern);
                                emptyTemplate.setController(handlerMethod.getBeanType().getName());

                                this.projectApi(emptyTemplate, requestMappingInfo, handlerMethod);

                                return emptyTemplate;
                            });
                }).collect(Collectors.toList());

        log.info("RESTdoc collect api empty templates size {} ", httpApiList.size());
    }

    public List<HttpApiDescriptor> getHttpApiList() {
        return httpApiList;
    }


    /**
     * Transparent application api endpoint interface
     *
     * @param emptyTemplate      Empty descriptor
     * @param handlerMethod      Endpoint api method
     * @param requestMappingInfo {@link RequestMappingInfo}
     * @see RequestMapping
     * @see javax.servlet.http.HttpServletRequest
     * @see org.springframework.web.bind.annotation.PathVariable
     * @see org.springframework.web.bind.annotation.RequestParam
     * @see org.springframework.web.bind.annotation.RequestPart
     * @see org.springframework.web.bind.annotation.MatrixVariable
     * @see org.springframework.web.bind.annotation.RequestBody
     * @see org.springframework.web.bind.annotation.CookieValue
     * @see org.springframework.web.bind.annotation.RequestHeader
     * @see MultipartFile
     */
    private void projectApi(HttpApiDescriptor emptyTemplate,
                            RequestMappingInfo requestMappingInfo,
                            HandlerMethod handlerMethod) {

        emptyTemplate.setName(requestMappingInfo.getName() == null ?
                handlerMethod.getMethod().getName() : requestMappingInfo.getName());

        MethodParameter[] parameters = handlerMethod.getMethodParameters();

        boolean hasRequestPartAnnotation = Stream.of(parameters)
                .flatMap(t -> Stream.of(t.getParameterAnnotations()))
                .anyMatch(t -> t.annotationType() == RequestPart.class);

        boolean hasFileParam = Stream.of(parameters)
                .anyMatch(t -> ResolverUtil.isFileType(t.getParameterType()));

        if (hasFileParam || hasRequestPartAnnotation) emptyTemplate.setRequireFile(true);

        for (MethodParameter parameter : parameters) {
            Annotation[] annotations = parameter.getParameterAnnotations();
            if (annotations.length > 0) {
                for (Annotation annotation : annotations) {
                    ResolverProxy.resolve(emptyTemplate, handlerMethod, requestMappingInfo, parameter, annotation);
                }
            }
        }

        Class<?> returnType = handlerMethod.getMethod().getReturnType();

        // Response Type
        if (returnType != ResponseEntity.class) {
            Object responseInstance = ResolverUtil.instantiate(handlerMethod.getMethod().getReturnType());
            if (responseInstance != null) {
                HttpApiDescriptor.ParameterDescriptor parameterDescriptor =
                        new HttpApiDescriptor.ParameterDescriptor();

                parameterDescriptor.setType(FieldType.OBJECT);
                try {
                    emptyTemplate.setResponseBodyDescriptor(parameterDescriptor);
                    parameterDescriptor.setSupplementary(responseInstance);
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }

        // RequestContentType
        ConsumesRequestCondition consumesCondition = requestMappingInfo.getConsumesCondition();
        List<HttpApiDescriptor.ParameterDescriptor> requestMimeTypeDescriptor = emptyTemplate.getRequestHeaderParameters()
                .getOrDefault(HttpHeaders.CONTENT_TYPE, new ArrayList<>());

        if (!consumesCondition.isEmpty() && !requestMimeTypeDescriptor.isEmpty()) {
            // Acceptable media type
            Set<MediaType> mts = consumesCondition.getConsumableMediaTypes();
            Set<HttpApiDescriptor.ParameterDescriptor> pds = mts.stream()
                    .map(t -> {
                        HttpApiDescriptor.ParameterDescriptor pd =
                                new HttpApiDescriptor.ParameterDescriptor();
                        pd.setDefaultValue(t.toString());

                        return pd;
                    }).collect(Collectors.toSet());

            requestMimeTypeDescriptor.addAll(pds);
        } else {
            // setup fixed
            HttpApiDescriptor.ParameterDescriptor pd =
                    new HttpApiDescriptor.ParameterDescriptor();

            if (HttpMethod.GET.name().equals(emptyTemplate.getMethod()))
                pd.setDefaultValue(new MimeType("*", "*").toString());
            else pd.setDefaultValue(MediaType.APPLICATION_JSON_VALUE);

            requestMimeTypeDescriptor.add(pd);
        }

        emptyTemplate.getRequestHeaderParameters().put(HttpHeaders.CONTENT_TYPE, requestMimeTypeDescriptor);

        //
        HttpApiDescriptor.KeyValuePair respContentMimeTypePair = new HttpApiDescriptor.KeyValuePair();
        respContentMimeTypePair.setName(HttpHeaders.CONTENT_TYPE);
        respContentMimeTypePair.setRequire(true);
        respContentMimeTypePair.setDefaultValue(MediaType.APPLICATION_JSON_VALUE);

        try {
            System.err.println(mapper.writeValueAsString(emptyTemplate));
        } catch (Exception e) {
        }
    }

}
