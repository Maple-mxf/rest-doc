package restdoc.client.api.model;

import java.util.Objects;

/**
 * The class  DubboInvocationResult
 *
 * @author Maple
 */
public class DubboInvocationResult extends InvocationResult {

    private String returnValue = null;
    private String returnValueType = Void.class.toString();


    public DubboInvocationResult() {
    }

    public DubboInvocationResult(Boolean isSuccessful, String exceptionMsg,
                                 Invocation invocation,
                                 String returnValueType,
                                 String returnValue
    ) {
        super(isSuccessful, exceptionMsg, invocation);
        this.returnValue = returnValue;
        this.returnValueType = returnValueType;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    public String getReturnValueType() {
        return returnValueType;
    }

    public void setReturnValueType(String returnValueType) {
        this.returnValueType = returnValueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DubboInvocationResult that = (DubboInvocationResult) o;
        return Objects.equals(returnValue, that.returnValue) &&
                Objects.equals(returnValueType, that.returnValueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnValue, returnValueType);
    }
}
