package restdoc.client.restweb.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import restdoc.rpc.client.common.model.FieldType;
import restdoc.rpc.client.common.model.http.HttpApiDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


/**
 * RequestBodyResolver
 *
 * @see RequestBody
 */
final class RequestBodyResolver implements Resolver {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void resolve(HttpApiDescriptor emptyTemplate,
                        HandlerMethod handlerMethod,
                        RequestMappingInfo requestMappingInfo,
                        MethodParameter parameter,
                        Annotation annotation) {

        RequestBody requestBody = (RequestBody) annotation;
        Class<?> parameterType = parameter.getParameterType();

        HttpApiDescriptor.KeyValuePair pair = emptyTemplate.getRequestHeaders()
                .stream()
                .filter(t -> HttpHeaders.CONTENT_TYPE.equals(t.getName()))
                .findFirst()
                .orElse(new HttpApiDescriptor.KeyValuePair());

        emptyTemplate.getRequestHeaders().remove(pair);
        pair.setName(HttpHeaders.CONTENT_TYPE);
        pair.setRequire(true);


        if (ResolverUtil.isFileType(parameterType)) {
            emptyTemplate.setRequireFile(true);
            HttpApiDescriptor.ParameterDescriptor pd =
                    new HttpApiDescriptor.ParameterDescriptor();

            pd.setName(parameter.getParameterName());
            pd.setRequire(requestBody.required());
            pd.setType(FieldType.FILE);

            emptyTemplate.getRequestBodyParameters().add(pd);

            pair.setDefaultValue(MediaType.MULTIPART_FORM_DATA_VALUE);
            emptyTemplate.addRequestHeader(pair);

        } else {
            if (!ResolverUtil.isPrimitive(parameterType)) {

                if (!emptyTemplate.isRequireFile()) {
                    Object instantiate = ResolverUtil.instantiate(parameter.getParameterType());

                    HttpApiDescriptor.ParameterDescriptor pd =
                            new HttpApiDescriptor.ParameterDescriptor();

                    pd.setType(FieldType.OBJECT);
                    pd.setRequire(requestBody.required());
                    pd.setName(parameter.getParameterName());
                    pd.setDefaultValue(instantiate);

                    try {
                        pd.setSupplementary(mapper.writeValueAsString(instantiate));
                    } catch (Exception ignored) {
                    }

                    emptyTemplate.getRequestBodyParameters().add(pd);
                }
                else {
                    pair.setDefaultValue(MediaType.MULTIPART_FORM_DATA_VALUE);
                    emptyTemplate.addRequestHeader(pair);

                    Field[] fields = parameterType.getDeclaredFields();

                    for (Field field : fields) {
                        field.setAccessible(true);

                        HttpApiDescriptor.ParameterDescriptor pd =
                                new HttpApiDescriptor.ParameterDescriptor();

                        pd.setName(field.getName());
                        pd.setRequire(true);

                        if (ResolverUtil.isPrimitive(field.getType())) {
                            pd.setType(ResolverUtil.getType(field.getType()));
                            emptyTemplate.getRequestBodyParameters().add(pd);
                        } else if (ResolverUtil.isFileType(field.getType())) {
                            pd.setType(FieldType.FILE);
                            emptyTemplate.getRequestBodyParameters().add(pd);
                        }
                    }
                }
            }
        }
    }
}
