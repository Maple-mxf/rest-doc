package restdoc.client.api;

import restdoc.remoting.protocol.RemotingCommand;

import java.util.Objects;

/**
 * Base invoke result class
 *
 * @author Maple
 */
public class InvokeResult {

    private RemotingCommand response;
    private long completeTime;

    public InvokeResult(RemotingCommand response, long completeTime) {
        this.response = response;
        this.completeTime = completeTime;
    }

    public RemotingCommand getResponse() {
        return response;
    }

    public void setResponse(RemotingCommand response) {
        this.response = response;
    }

    public long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(long completeTime) {
        this.completeTime = completeTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvokeResult that = (InvokeResult) o;
        return completeTime == that.completeTime &&
                Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(response, completeTime);
    }
}
