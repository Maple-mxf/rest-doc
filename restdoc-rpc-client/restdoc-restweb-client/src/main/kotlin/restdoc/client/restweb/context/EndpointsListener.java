package restdoc.client.restweb.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.condition.MediaTypeExpression;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import restdoc.remoting.common.RestWebApiDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The EndpointsListener provided report client api list info to server
 *
 * @author Maple
 */
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger log = LoggerFactory.getLogger(EndpointsListener.class);

    @Deprecated
    private ObjectMapper mapper = new ObjectMapper();

    private List<RestWebApiDescriptor> restWebExposedAPIList;

    private final Environment environment;

    private final Set<Class<?>> springAcceptAnnotationTypes = ofSet(
            CookieValue.class,
            RequestParam.class,
            PathVariable.class,
            RequestBody.class,
            RequestPart.class,
            MatrixVariable.class,
            CookieValue.class,
            RequestHeader.class
    );

    private static class AcceptableParameter {
        private Annotation annotation;
        private MethodParameter methodParameter;

        public AcceptableParameter(Annotation annotation, MethodParameter methodParameter) {
            this.annotation = annotation;
            this.methodParameter = methodParameter;
        }

        public Annotation getAnnotation() {
            return annotation;
        }

        public void setAnnotation(Annotation annotation) {
            this.annotation = annotation;
        }

        public MethodParameter getMethodParameter() {
            return methodParameter;
        }

        public void setMethodParameter(MethodParameter methodParameter) {
            this.methodParameter = methodParameter;
        }
    }

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
     * @return {@link RestWebApiDescriptor}
     * @see RequestMapping
     * @see javax.servlet.http.HttpServletRequest
     * transparentApi
     * @see org.springframework.web.bind.annotation.PathVariable
     * @see org.springframework.web.bind.annotation.RequestParam
     * @see org.springframework.web.bind.annotation.RequestPart
     * @see org.springframework.web.bind.annotation.MatrixVariable
     * @see org.springframework.web.bind.annotation.RequestBody
     * @see org.springframework.web.bind.annotation.CookieValue
     * @see org.springframework.web.bind.annotation.RequestHeader
     * @see MultipartFile
     */
    private RestWebApiDescriptor transparentApi(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
        RestWebApiDescriptor emptyDescriptor = new RestWebApiDescriptor();
        emptyDescriptor.setName(requestMappingInfo.getName());

        Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();

        boolean isAllowRequestBody = true;

        if (methods.contains(RequestMethod.GET))
            isAllowRequestBody = false;

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

                        String pathVariableName = pathVariable.name().isEmpty() ?
                                pathVariable.value() :
                                pathVariable.value();
                        if (pathVariableName.isEmpty()) pathVariableName = parameter.getParameterName();

                        parameterDescriptor.setRequire(pathVariable.required());
                        parameterDescriptor.setName(pathVariableName);

                        emptyDescriptor.getPathVariableParameters()
                                .add(parameterDescriptor);
                    } else if (annotation.annotationType().equals(RequestParam.class)) {

                        RequestParam requestParam = (RequestParam) annotation;
                        parameterDescriptor.setRequire(requestParam.required());
                        parameterDescriptor.setDefaultValue(requestParam.defaultValue());

                        if (!isAllowRequestBody) {
                            emptyDescriptor.getQueryParamParameters().add(parameterDescriptor);
                        } else {
                            emptyDescriptor.getRequestBodyParameters().add(parameterDescriptor);
                        }
                    }
                    // If has request part
                    else if (annotation.annotationType().equals(RequestPart.class)) {
                        RequestPart requestPart = (RequestPart) annotation;

                        String requestPartName = requestPart.name().isEmpty() ?
                                requestPart.value() :
                                requestPart.name();
                        if (requestPartName.isEmpty()) requestPartName = parameter.getParameterName();

                        parameterDescriptor.setName(requestPartName);
                        parameterDescriptor.setRequire(requestPart.required());

                        if (isPrimitive(parameter.getParameterType())) {
                            parameterDescriptor.setType(parameter.getParameterType().getName());
                        } else {
                            // If is map TODO
                            if (Arrays.stream(parameter.getParameterType().getGenericInterfaces())
                                    .noneMatch(t -> t == Map.class)) {
                                Object dtoInstance = instantiate(parameter.getParameterType());
                                try {
                                    String valueAsString = mapper.writeValueAsString(dtoInstance);
                                    parameterDescriptor.setSupplementary(valueAsString);
                                    parameterDescriptor.setType(Object.class.getName());

                                } catch (Exception ignored) {
                                    // TODO
                                    ignored.printStackTrace();
                                }
                            }
                        }

                        emptyDescriptor.getRequestBodyParameters().add(parameterDescriptor);
                    } else if (annotation.annotationType().equals(MatrixVariable.class)) {

                        MatrixVariable matrixVariable = (MatrixVariable) annotation;

                        parameterDescriptor.setRequire(matrixVariable.required());
                        String matrixVariableName = matrixVariable.name().isEmpty() ?
                                matrixVariable.value() : matrixVariable.name();
                        if (matrixVariableName.isEmpty()) matrixVariableName = parameter.getParameterName();

                        parameterDescriptor.setName(matrixVariableName);
                        parameterDescriptor.setDefaultValue(matrixVariable.defaultValue());

                        if (!ValueConstants.DEFAULT_NONE.equals(matrixVariable.pathVar())) {
                            parameterDescriptor.setSupplementary(matrixVariable.pathVar());
                        }

                        emptyDescriptor.getMatrixVariableParameters().add(parameterDescriptor);
                    } else if (annotation.annotationType().equals(RequestBody.class)) {

                        RequestBody requestBody = (RequestBody) annotation;
                        parameterDescriptor.setRequire(requestBody.required());
                        if (!isPrimitive(parameter.getParameterType())) {
                            if (Arrays.stream(parameter.getParameterType().getGenericInterfaces())
                                    .noneMatch(t -> t == Map.class)) {

                                Object dtoInstance = instantiate(parameter.getParameterType());
                                try {
                                    String valueAsString = mapper.writeValueAsString(dtoInstance);
                                    parameterDescriptor.setSupplementary(valueAsString);
                                    parameterDescriptor.setType(Object.class.getName());
                                } catch (Exception ignored) {
                                    // TODO
                                    ignored.printStackTrace();
                                }
                            }
                        }

                        emptyDescriptor.getRequestBodyParameters().add(parameterDescriptor);
                    } else if (annotation.annotationType().equals(CookieValue.class)) {

                        CookieValue cookieValue = (CookieValue) annotation;
                        parameterDescriptor.setRequire(cookieValue.required());
                        parameterDescriptor.setName(HttpHeaders.COOKIE);
                        parameterDescriptor.setDefaultValue(cookieValue.defaultValue());

                        List<RestWebApiDescriptor.ParameterDescriptor> cookieValues = emptyDescriptor.getRequestHeaderParameters()
                                .getOrDefault(HttpHeaders.COOKIE, new ArrayList<>());

                        String cookieName = cookieValue.name().isEmpty() ? cookieValue.value() : cookieValue.name();

                        if (!cookieName.isEmpty()) {
                            RestWebApiDescriptor.ParameterDescriptor cookieDescriptor
                                    = new RestWebApiDescriptor.ParameterDescriptor(cookieValue.name());

                            cookieDescriptor.setDefaultValue(cookieValue.defaultValue());
                            cookieValues.add(cookieDescriptor);

                            emptyDescriptor.getRequestHeaderParameters().put(HttpHeaders.COOKIE, cookieValues);
                        }

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

        Class<?> returnType = handlerMethod.getMethod().getReturnType();

        if (returnType != ResponseEntity.class) {
            Object responseInstance = instantiate(handlerMethod.getMethod().getReturnType());
            if (responseInstance != null) {
                RestWebApiDescriptor.ParameterDescriptor parameterDescriptor =
                        new RestWebApiDescriptor.ParameterDescriptor();
                try {
                    parameterDescriptor.setSupplementary(mapper.writeValueAsString(responseInstance));
                    emptyDescriptor.setResponseBodyDescriptor(parameterDescriptor);
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }

        try {
            System.err.println(mapper.writeValueAsString(emptyDescriptor));
        }catch (Exception e){e.printStackTrace();}

        return emptyDescriptor;
    }

    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULT_VALUE = new HashMap<Class<?>, Object>() {
        {
            this.put(Void.class, null);
            this.put(Boolean.class, false);
            this.put(boolean.class, false);
            this.put(Character.class, 'A');
            this.put(char.class, 'A');
            this.put(Byte.class, 0);
            this.put(byte.class, 0);
            this.put(Short.class, 0);
            this.put(short.class, 0);
            this.put(Integer.class, 0);
            this.put(int.class, 0);
            this.put(Long.class, 0L);
            this.put(long.class, 0L);
            this.put(Float.class, 0.0F);
            this.put(float.class, 0.0F);
            this.put(Double.class, 0.0D);
            this.put(double.class, 0.0D);
            this.put(String.class, "");
            this.put(BigDecimal.class, BigDecimal.valueOf(0L));
            this.put(BigInteger.class, BigInteger.valueOf(0L));
            this.put(Date.class, new Date());
            this.put(Object.class, null);
            this.put(MultipartFile.class, null);
        }
    };

    private static final Set<Class<?>> SIMPLE_TYPES = ofSet(
            Void.class,
            Boolean.class,
            Character.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            String.class,
            BigDecimal.class,
            BigInteger.class,
            Date.class,
            Object.class
    );

    @SafeVarargs
    private static <T> Set<T> ofSet(T... eles) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, eles);
        return set;
    }

    private static boolean isPrimitive(Class<?> type) {
        if (type.isPrimitive()) return true;
        if (SIMPLE_TYPES.contains(type)) {
            return true;
        }
        return type.equals(MultipartFile.class);
    }

    private static Object instantiate(Class<?> beanType) {
        try {
            Constructor<?>[] constructors = beanType.getConstructors();
            Object dtoInstance = Arrays.stream(constructors).filter(ct -> ct.getParameterCount() == 0)
                    .findAny()
                    .map(ct -> {
                        try {
                            return ct.newInstance();
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                            return null;
                        }
                    })
                    .orElse(null);

            if (dtoInstance == null) return null;

            Field[] fields = beanType.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                if (isPrimitive(fieldType)) {
                    Object defaultSampleValue = PRIMITIVE_DEFAULT_VALUE.get(fieldType);
                    field.set(dtoInstance, defaultSampleValue);
                } else {
                    Object dtoFieldInstance = instantiate(fieldType);
                    field.set(dtoInstance, dtoFieldInstance);
                }
            }
            return dtoInstance;
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

}
