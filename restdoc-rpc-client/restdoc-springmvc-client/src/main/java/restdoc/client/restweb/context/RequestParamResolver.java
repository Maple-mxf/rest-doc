package restdoc.client.restweb.context;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import restdoc.rpc.client.common.model.http.HttpApiDescriptor;

import java.lang.annotation.Annotation;


/**
 * RequestParamResolver
 *
 * @see org.springframework.web.bind.annotation.RequestParam
 */
class RequestParamResolver implements Resolver {

    @Override
    public void resolve(HttpApiDescriptor emptyTemplate,
                        HandlerMethod handlerMethod,
                        RequestMappingInfo requestMappingInfo,
                        MethodParameter parameter,
                        Annotation annotation) {

        RequestParam requestParam = (RequestParam) annotation;
        Class<?> parameterType = parameter.getParameterType();

        if (ResolverUtil.isPrimitive(parameterType) && !Object.class.equals(parameter)) {

            String name = requestParam.name();
            if (name.isEmpty()) name = parameter.getParameterName();

            HttpApiDescriptor.ParameterDescriptor pd =
                    new HttpApiDescriptor.ParameterDescriptor(name);

            pd.setRequire(requestParam.required());
            pd.setDefaultValue(requestParam.defaultValue());
            pd.setType(ResolverUtil.getType(parameterType));

            emptyTemplate.getQueryParamParameters().add(pd);
        }

    }
}
