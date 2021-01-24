package restdoc.client.restweb.context;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import restdoc.rpc.client.common.model.http.HttpApiDescriptor;

import java.lang.annotation.Annotation;


/**
 * MatrixVariableResolver
 *
 * @see org.springframework.web.bind.annotation.MatrixVariable
 */
final class MatrixVariableResolver implements Resolver {

    @Override
    public void resolve(HttpApiDescriptor emptyTemplate,
                        HandlerMethod handlerMethod,
                        RequestMappingInfo requestMappingInfo,
                        MethodParameter parameter,
                        Annotation annotation) {

        MatrixVariable matrixVariable = (MatrixVariable) annotation;

        String name = matrixVariable.name();
        if (name.isEmpty()) name = parameter.getParameterName();

        HttpApiDescriptor.ParameterDescriptor pd =
                new HttpApiDescriptor.ParameterDescriptor(name);

        pd.setType( ResolverUtil.getType(parameter.getParameterType()));
        pd.setRequire(matrixVariable.required());
        pd.setDefaultValue(matrixVariable.defaultValue());
        pd.setSupplementary(matrixVariable.pathVar());

        emptyTemplate.getMatrixVariableParameters().add(pd);
    }
}
