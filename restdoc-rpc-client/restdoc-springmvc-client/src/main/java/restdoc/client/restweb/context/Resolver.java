package restdoc.client.restweb.context;

import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import restdoc.rpc.client.common.model.http.HttpApiDescriptor;

import java.lang.annotation.Annotation;

/**
 * HttpParamResolver
 */
@FunctionalInterface
interface Resolver {

    void resolve(HttpApiDescriptor emptyTemplate,
                 HandlerMethod handlerMethod,
                 RequestMappingInfo requestMappingInfo,
                 MethodParameter parameter, Annotation annotation);
}
