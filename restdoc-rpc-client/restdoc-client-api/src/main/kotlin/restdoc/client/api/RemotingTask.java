package restdoc.client.api;

import restdoc.remoting.InvokeCallback;
import restdoc.remoting.protocol.RemotingCommand;

import java.util.Objects;


/**
 * The class RemotingTask
 *
 * @author Maple
 */
public class RemotingTask {

    private String taskId;

    private RemotingTaskType type;

    private RemotingCommand request;

    private long timeoutMills;

    private InvokeCallback invokeCallback;

    public RemotingTask(String taskId, RemotingTaskType type, RemotingCommand request, long timeoutMills, InvokeCallback invokeCallback) {
        this.taskId = taskId;
        this.type = type;
        this.request = request;
        this.timeoutMills = timeoutMills;
        this.invokeCallback = invokeCallback;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public RemotingTaskType getType() {
        return type;
    }

    public void setType(RemotingTaskType type) {
        this.type = type;
    }

    public RemotingCommand getRequest() {
        return request;
    }

    public void setRequest(RemotingCommand request) {
        this.request = request;
    }

    public long getTimeoutMills() {
        return timeoutMills;
    }

    public void setTimeoutMills(long timeoutMills) {
        this.timeoutMills = timeoutMills;
    }

    public InvokeCallback getInvokeCallback() {
        return invokeCallback;
    }

    public void setInvokeCallback(InvokeCallback invokeCallback) {
        this.invokeCallback = invokeCallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemotingTask that = (RemotingTask) o;
        return timeoutMills == that.timeoutMills &&
                Objects.equals(taskId, that.taskId) &&
                type == that.type &&
                Objects.equals(request, that.request) &&
                Objects.equals(invokeCallback, that.invokeCallback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, type, request, timeoutMills, invokeCallback);
    }
}
