package restdoc.client.api.model;

import restdoc.remoting.protocol.RemotingSerializable;


/**
 * InvocationResult
 *
 * @author Maple
 */
public class InvocationResult extends RemotingSerializable {

    private Boolean isSuccessful = true;
    private String exceptionMsg;

    private Invocation invocation;


    public InvocationResult() {
    }


    public InvocationResult(Boolean isSuccessful, String exceptionMsg, Invocation invocation) {
        this.isSuccessful = isSuccessful;
        this.exceptionMsg = exceptionMsg;
        this.invocation = invocation;
    }

    public Boolean getSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(Boolean successful) {
        isSuccessful = successful;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }
}
