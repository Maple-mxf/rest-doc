package restdoc.client.restweb.context;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import restdoc.rpc.client.common.model.http.HttpApiDescriptor;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ResolverProxy
 */
final class ResolverProxy {

    private static final Map<Class<? extends Annotation>, Resolver> RESOLVERS = new HashMap<>();

    static {
        RESOLVERS.put(MatrixVariable.class, new MatrixVariableResolver());
        RESOLVERS.put(PathVariable.class, new PathVariableResolver());
        RESOLVERS.put(RequestBody.class, new RequestBodyResolver());
        RESOLVERS.put(RequestHeader.class, new RequestHeaderResolver());
        RESOLVERS.put(CookieValue.class, new CookieValueResolver());
        RESOLVERS.put(RequestParam.class, new RequestParamResolver());
        RESOLVERS.put(RequestPart.class, new RequestPartResolver());
    }

    static void resolve(HttpApiDescriptor emptyTemplate,
                        HandlerMethod handlerMethod,
                        RequestMappingInfo requestMappingInfo,
                        MethodParameter parameter,
                        Annotation annotation) {

        Class<? extends Annotation> annotationType = annotation.annotationType();

        Optional.of(RESOLVERS.get(annotationType)).get()
                .resolve(emptyTemplate, handlerMethod, requestMappingInfo, parameter, annotation);

    }
}
