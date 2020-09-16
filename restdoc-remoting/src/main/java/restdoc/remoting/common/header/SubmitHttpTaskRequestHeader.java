package restdoc.remoting.common.header;

import restdoc.remoting.CommandCustomHeader;
import restdoc.remoting.exception.RemotingCommandException;

/**
 * HttpTaskRequestHeader
 */
@Deprecated
public class SubmitHttpTaskRequestHeader implements CommandCustomHeader {

    private String taskId;

    @Override
    public void checkFields() throws RemotingCommandException {
        if (taskId == null)
            throw new RemotingCommandException("taskId require");
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
