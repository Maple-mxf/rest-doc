package restdoc.client.dubbo.model;

import org.apache.dubbo.common.utils.ReflectUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.apache.dubbo.rpc.Constants.$INVOKE;
import static org.apache.dubbo.rpc.Constants.$INVOKE_ASYNC;

/**
 *
 */
public class MethodDescriptor {
    private final Method method;
    //    private final boolean isCallBack;
//    private final boolean isFuture;
    private final String paramDesc;
    // duplicate filed as paramDesc, but with different format.
    private final String[] compatibleParamSignatures;
    private final String[] parameterNames;
    private final Class<?>[] parameterClasses;
    private final Class<?> returnClass;
    private final Type[] returnTypes;
    private final String methodName;
    private final boolean generic;

    public MethodDescriptor(Method method) {
        this.method = method;
        this.parameterClasses = method.getParameterTypes();
        this.returnClass = method.getReturnType();
        this.returnTypes = ReflectUtils.getReturnTypes(method);
        this.paramDesc = ReflectUtils.getDesc(parameterClasses);
        this.compatibleParamSignatures = Stream.of(parameterClasses)
                .map(Class::getName)
                .toArray(String[]::new);
        this.methodName = method.getName();
        this.generic = (methodName.equals($INVOKE) || methodName.equals($INVOKE_ASYNC)) && parameterClasses.length == 3;

        Parameter[] parameters = method.getParameters();
        parameterNames = Arrays.stream(parameters).map(Parameter::getName).toArray(String[]::new);

        System.err.println(parameterNames);
    }

    public boolean matchParams(String params) {
        return paramDesc.equalsIgnoreCase(params);
    }

    public Method getMethod() {
        return method;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public String[] getCompatibleParamSignatures() {
        return compatibleParamSignatures;
    }

    public Class<?>[] getParameterClasses() {
        return parameterClasses;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    public Type[] getReturnTypes() {
        return returnTypes;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isGeneric() {
        return generic;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }
}
