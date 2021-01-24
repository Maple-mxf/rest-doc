package restdoc.client.restweb.context;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import restdoc.rpc.client.common.model.http.HttpApiDescriptor;

import java.lang.annotation.Annotation;


/**
 * @see org.springframework.web.bind.annotation.RequestHeader
 * @see org.springframework.http.HttpHeaders
 */
final class RequestHeaderResolver implements Resolver {

    @Override
    public void resolve(HttpApiDescriptor emptyTemplate,
                        HandlerMethod handlerMethod,
                        RequestMappingInfo requestMappingInfo,
                        MethodParameter parameter, Annotation annotation) {

        RequestHeader requestHeader = (RequestHeader) annotation;

        String name = requestHeader.name();
        if (name.isEmpty()) name = parameter.getParameterName();

        String finalName = name;


        if (emptyTemplate.getRequestHeaders()
                .stream()
                .noneMatch(t -> t.getName().equals(finalName))) {

            HttpApiDescriptor.KeyValuePair pair = new HttpApiDescriptor.KeyValuePair();
            pair.setName(name);
            pair.setDefaultValue(requestHeader.defaultValue());
            pair.setRequire(requestHeader.required());

            emptyTemplate.addRequestHeader(pair);
        }

    }
}
