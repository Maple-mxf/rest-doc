package restdoc.client.restweb.context;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import restdoc.rpc.client.common.model.http.HttpApiDescriptor;

import java.lang.annotation.Annotation;


/**
 * CookieValueResolver
 *
 * @see org.springframework.web.bind.annotation.CookieValue
 */
final class CookieValueResolver implements Resolver {

    @Override
    public void resolve(HttpApiDescriptor emptyTemplate,
                        HandlerMethod handlerMethod,
                        RequestMappingInfo requestMappingInfo,
                        MethodParameter parameter,
                        Annotation annotation) {

        CookieValue cookieValue = (CookieValue) annotation;

        String name = cookieValue.name();
        if (name.isEmpty()) name = parameter.getParameterName();

        HttpApiDescriptor.KeyValuePair pair = emptyTemplate.getRequestHeaders()
                .stream()
                .filter(t -> HttpHeaders.COOKIE.equals(t.getName()))
                .findFirst()
                .orElseGet(() -> {
                    HttpApiDescriptor.KeyValuePair newPair =
                            new HttpApiDescriptor.KeyValuePair();

                    newPair.setName(HttpHeaders.COOKIE);
                    newPair.setRequire(true);
                    newPair.setDefaultValue("");
                    return newPair;
                });

        emptyTemplate.getRequestHeaders().remove(pair);
        emptyTemplate.addRequestHeader(pair);

        String cookieValueString = pair.getDefaultValue().toString();

        pair.setDefaultValue(String.join("; ", cookieValueString, String.format("%s=%s", name, cookieValue.defaultValue())));
    }
}
