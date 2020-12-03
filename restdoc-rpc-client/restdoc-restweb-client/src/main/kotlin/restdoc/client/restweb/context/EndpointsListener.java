package restdoc.client.restweb.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import restdoc.remoting.common.RestWebApiDescriptor;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The EndpointsListener provided report client api list info to server
 *
 * @author Maple
 */
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger log = LoggerFactory.getLogger(EndpointsListener.class);

    private List<RestWebApiDescriptor> restWebExposedAPIList;

    private final Environment environment;

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
        this.restWebExposedAPIList = handlerMethods.entrySet()
                .stream()
                .flatMap(hm -> {
                    RequestMappingInfo requestMappingInfo = hm.getKey();
                    HandlerMethod handlerMethod = hm.getValue();

                    transparentApi(requestMappingInfo, handlerMethod);

                    return requestMappingInfo.getPatternsCondition()
                            .getPatterns()
                            .stream()
                            .filter(pattern -> !"/error".equals(pattern))
                            .map(pattern -> String.join("", contextPath, pattern))
                            .map(pattern -> {

                                RestWebApiDescriptor emptyTemplate = new RestWebApiDescriptor();
                                emptyTemplate.setSupportMethod(requestMappingInfo.getMethodsCondition()
                                        .getMethods()
                                        .stream()
                                        .map(Enum::name)
                                        .toArray(String[]::new));
                                emptyTemplate.setFunction(handlerMethod.toString());
                                emptyTemplate.setPattern(pattern);
                                emptyTemplate.setController(handlerMethod.getBeanType().toString());

                                emptyTemplate.setConsumer(
                                        requestMappingInfo.getConsumesCondition()
                                                .getExpressions()
                                                .stream()
                                                .filter(MediaTypeExpression::isNegated)
                                                .map(MediaTypeExpression::getMediaType)
                                                .map(MimeType::getType)
                                                .toArray(String[]::new)
                                );

                                emptyTemplate.setProduces(requestMappingInfo.getProducesCondition()
                                        .getExpressions()
                                        .stream()
                                        .filter(MediaTypeExpression::isNegated)
                                        .map(MediaTypeExpression::getMediaType)
                                        .map(MimeType::getType)
                                        .toArray(String[]::new));

                                emptyTemplate.setUriVarFields(Arrays.stream(pattern.split("/"))
                                        .filter(snippet -> snippet.matches("^[\\{][a-zA-Z]+[0-9A-Za-z]*[\\}]$"))
                                        .map(snippet -> snippet.replaceFirst("\\{", "")
                                                .replaceAll("\\}", ""))
                                        .toArray(String[]::new));

                                return emptyTemplate;
                            });
                }).collect(Collectors.toList());

        log.info("RESTDOC-CLIENT collect api empty templates {} ", restWebExposedAPIList);
    }

    public List<RestWebApiDescriptor> getRestWebExposedAPIList() {
        return restWebExposedAPIList;
    }


    /**
     * @see RequestMapping
     * transparentApi
     *
     * @return {@link RestWebApiDescriptor}
     * @see org.springframework.web.bind.annotation.PathVariable
     * @see org.springframework.web.bind.annotation.RequestParam
     * @see org.springframework.web.bind.annotation.RequestPart
     * @see org.springframework.web.bind.annotation.MatrixVariable
     * @see org.springframework.web.bind.annotation.RequestBody
     * @see org.springframework.web.bind.annotation.CookieValue
     * @see org.springframework.web.bind.annotation.RequestHeader
     */
    private RestWebApiDescriptor transparentApi(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
        RestWebApiDescriptor emptyDescriptor = new RestWebApiDescriptor();
        emptyDescriptor.setName(requestMappingInfo.getName());

        Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();

        ParamsRequestCondition paramsCondition = requestMappingInfo.getParamsCondition();
        ConsumesRequestCondition consumesCondition = requestMappingInfo.getConsumesCondition();
        ProducesRequestCondition producesCondition = requestMappingInfo.getProducesCondition();
        HeadersRequestCondition headersCondition = requestMappingInfo.getHeadersCondition();
        RequestCondition<?> customCondition = requestMappingInfo.getCustomCondition();

        boolean isGetMethod = methods.isEmpty();
        boolean isAllowRequestBody = true;

        // content-type=application/*  TODO
        @Deprecated
        boolean isRequireFormDataHeader =
                consumesCondition.getConsumableMediaTypes().contains(MediaType.MULTIPART_FORM_DATA) ||
                        consumesCondition.getConsumableMediaTypes().contains(MediaType.APPLICATION_FORM_URLENCODED);

        if (methods.contains(RequestMethod.GET))
            isAllowRequestBody = false;
        if (isRequireFormDataHeader)
            isAllowRequestBody = true;

        MethodParameter[] parameters = handlerMethod.getMethodParameters();

        for (MethodParameter parameter : parameters) {
            if (parameter.getParameterAnnotations().length > 0) {
                String parameterName = parameter.getParameterName();
                RestWebApiDescriptor.ParameterDescriptor parameterDescriptor =
                        new RestWebApiDescriptor.ParameterDescriptor(parameterName);
                parameterDescriptor.setType(parameter.getParameterType().getName());

                Annotation[] annotations = parameter.getParameterAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().equals(PathVariable.class)) {
                        PathVariable pathVariable = (PathVariable) annotation;
                        parameterDescriptor.setRequire(pathVariable.required());
                        emptyDescriptor.getPathVariableParameters()
                                .add(parameterDescriptor);
                    } else if (annotation.annotationType().equals(RequestParam.class)) {
                        RequestParam requestParam = (RequestParam) annotation;
                        parameterDescriptor.setRequire(requestParam.required());
                        parameterDescriptor.setDefaultValue(requestParam.defaultValue());

                        if (!isAllowRequestBody) {
                            emptyDescriptor.getQueryParamParameters().add(parameterDescriptor);
                        } else {
                        }
                    } else if (annotation.annotationType().equals(RequestPart.class)) {

                    } else if (annotation.annotationType().equals(MatrixVariable.class)) {
                        MatrixVariable matrixVariable = (MatrixVariable) annotation;
                        parameterDescriptor.setRequire(matrixVariable.required());
                        parameterDescriptor.setName(matrixVariable.name());
                        parameterDescriptor.setDefaultValue(matrixVariable.defaultValue());
                        emptyDescriptor.getMatrixVariableParameters().add(parameterDescriptor);

                    } else if (annotation.annotationType().equals(RequestBody.class)) {
                        RequestBody requestBody = (RequestBody) annotation;
                        parameterDescriptor.setRequire(requestBody.required());
                        emptyDescriptor.getRequestBodyParameters().add(parameterDescriptor);

                    } else if (annotation.annotationType().equals(CookieValue.class)) {
                        CookieValue cookieValue = (CookieValue) annotation;
                        parameterDescriptor.setRequire(cookieValue.required());
                        parameterDescriptor.setName("Cookie");
                        parameterDescriptor.setDefaultValue(cookieValue.defaultValue());

                        List<RestWebApiDescriptor.ParameterDescriptor> cookieValues = emptyDescriptor.getRequestHeaderParameters()
                                .getOrDefault("Cookie", new ArrayList<>());
                        emptyDescriptor.getRequestHeaderParameters().put("Cookie", cookieValues);

                    } else if (annotation.annotationType().equals(RequestHeader.class)) {
                        RequestHeader requestHeader = (RequestHeader) annotation;
                        parameterDescriptor.setDefaultValue(requestHeader.defaultValue());
                        parameterDescriptor.setRequire(requestHeader.required());
                        parameterDescriptor.setName(requestHeader.name());

                        emptyDescriptor.getRequestHeaderParameters()
                                .put(requestHeader.name(), Collections.singletonList(parameterDescriptor));
                    }
                }
            }
        }

        // consumesCondition
        List<RestWebApiDescriptor.ParameterDescriptor> acceptableContentTypes
                = emptyDescriptor.getRequestHeaderParameters().getOrDefault("Content-Type", new ArrayList<>());
        acceptableContentTypes.addAll(
                consumesCondition.getConsumableMediaTypes()
                        .stream()
                        .map(t -> {
                            RestWebApiDescriptor.ParameterDescriptor pd = new RestWebApiDescriptor.ParameterDescriptor(null, true);
                            pd.setRequireEqualsValue(t.toString());
                            return pd;
                        })
                        .collect(Collectors.toList())
        );
        emptyDescriptor.getRequestHeaderParameters().put("Content-Type", acceptableContentTypes);

        // headersCondition
        headersCondition.getExpressions()
                .stream()
                .filter(NameValueExpression::isNegated)
                .map(t -> {
                    RestWebApiDescriptor.ParameterDescriptor pd = new RestWebApiDescriptor.ParameterDescriptor(t.getName(), true);
                    pd.setRequireEqualsValue(t.getValue());
                    return pd;
                })
                .collect(Collectors.groupingBy(RestWebApiDescriptor.ParameterDescriptor::getName))
                .forEach((key, value) -> {
                    List<RestWebApiDescriptor.ParameterDescriptor> pds
                            = emptyDescriptor.getRequestHeaderParameters().getOrDefault(key, new ArrayList<>());
                    pds.addAll(value);
                    emptyDescriptor.getRequestHeaderParameters().put(key, pds);
                });

        // producesCondition
        List<RestWebApiDescriptor.ParameterDescriptor> produceContentTypes = emptyDescriptor.getResponseHeaderParameters()
                .getOrDefault("Content-Type", new ArrayList<>());
        produceContentTypes.addAll(
                producesCondition.getProducibleMediaTypes()
                        .stream()
                        .map(t -> {
                            RestWebApiDescriptor.ParameterDescriptor pd = new RestWebApiDescriptor.ParameterDescriptor(null, true);
                            pd.setRequireEqualsValue(t.toString());
                            return pd;
                        })
                        .collect(Collectors.toList()));
        emptyDescriptor.getResponseHeaderParameters().put("Content-Type", produceContentTypes);

        //

        return emptyDescriptor;
    }
}
