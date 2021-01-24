package restdoc.client.restweb.context;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import restdoc.rpc.client.common.model.http.HttpApiDescriptor;

import java.lang.annotation.Annotation;

final
class PathVariableResolver implements Resolver {

    @Override
    public void resolve(HttpApiDescriptor emptyTemplate,
                        HandlerMethod handlerMethod,
                        RequestMappingInfo requestMappingInfo,
                        MethodParameter parameter,
                        Annotation annotation) {

        PathVariable pathVariable = (PathVariable) annotation;

        String name = pathVariable.name();
        if (name.isEmpty()) name = parameter.getParameterName();

        HttpApiDescriptor.ParameterDescriptor pd =
                new HttpApiDescriptor.ParameterDescriptor(name);

        pd.setType( ResolverUtil.getType(parameter.getParameterType()));
        pd.setRequire(pathVariable.required());

        emptyTemplate.getPathVariableParameters().add(pd);
    }
}
