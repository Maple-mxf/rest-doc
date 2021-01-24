package restdoc.client.restweb.context;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import restdoc.rpc.client.common.model.FieldType;
import restdoc.rpc.client.common.model.http.HttpApiDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * The class RequestPartResolver
 *
 * @see HttpApiDescriptor
 */
final class RequestPartResolver implements Resolver {

    @Override
    public void resolve(HttpApiDescriptor emptyTemplate,
                        HandlerMethod handlerMethod,
                        RequestMappingInfo requestMappingInfo,
                        MethodParameter parameter, Annotation annotation) {

        Class<?> parameterType = parameter.getParameterType();

        RequestPart requestPart = (RequestPart) annotation;

        HttpApiDescriptor.KeyValuePair pair = emptyTemplate.getRequestHeaders()
                .stream()
                .filter(t -> HttpHeaders.CONTENT_TYPE.equals(t.getName()))
                .findFirst()
                .orElse(new HttpApiDescriptor.KeyValuePair());

        emptyTemplate.getRequestHeaders().remove(pair);
        pair.setName(HttpHeaders.CONTENT_TYPE);
        pair.setRequire(true);
        pair.setDefaultValue(MediaType.MULTIPART_FORM_DATA_VALUE);
        emptyTemplate.addRequestHeader(pair);


        String name = requestPart.name();
        if (name.isEmpty()) name = parameter.getParameterName();

        if (ResolverUtil.isFileType(parameterType)) {

            HttpApiDescriptor.ParameterDescriptor pd =
                    new HttpApiDescriptor.ParameterDescriptor();

            pd.setName(name);
            pd.setRequire(requestPart.required());
            pd.setType(FieldType.FILE);

            emptyTemplate.getRequestBodyParameters().add(pd);

        } else {
            if (ResolverUtil.isPrimitive(parameterType)) {
                Object defaultValue = ResolverUtil.getPrimitiveTypeDefaultValue(parameterType);

                HttpApiDescriptor.ParameterDescriptor pd =
                        new HttpApiDescriptor.ParameterDescriptor();

                pd.setType(ResolverUtil.getType(parameter.getParameterType()));
                pd.setRequire(requestPart.required());
                pd.setName(name);
                pd.setDefaultValue(defaultValue);

                emptyTemplate.getRequestBodyParameters().add(pd);

            } else {
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
