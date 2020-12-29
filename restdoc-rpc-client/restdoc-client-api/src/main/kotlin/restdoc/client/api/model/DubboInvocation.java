package restdoc.client.api.model;

import restdoc.remoting.protocol.RemotingSerializable;

import java.util.List;
import java.util.Objects;


/**
 * The class DubboInvocation
 *
 * @author Maple
 */
public class DubboInvocation extends RemotingSerializable implements Invocation {

    private String methodName;

    private List<ObjectHolder<Object>> parameters;

    @Deprecated
    private String refName;

    private String returnType;

    public DubboInvocation() {
    }

    public DubboInvocation(String methodName, List<ObjectHolder<Object>> parameters, String refName, String returnType) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.refName = refName;
        this.returnType = returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<ObjectHolder<Object>> getParameters() {
        return parameters;
    }

    public void setParameters(List<ObjectHolder<Object>> parameters) {
        this.parameters = parameters;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DubboInvocation that = (DubboInvocation) o;
        return Objects.equals(methodName, that.methodName) &&
                Objects.equals(parameters, that.parameters) &&
                Objects.equals(refName, that.refName) &&
                Objects.equals(returnType, that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, parameters, refName, returnType);
    }
}
