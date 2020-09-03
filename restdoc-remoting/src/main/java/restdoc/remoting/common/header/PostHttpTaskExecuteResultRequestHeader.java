package restdoc.remoting.common.header;

import restdoc.remoting.CommandCustomHeader;
import restdoc.remoting.exception.RemotingCommandException;

public class PostHttpTaskExecuteResultRequestHeader implements CommandCustomHeader {

    private String taskId;
    @Override
    public void checkFields() throws RemotingCommandException {
        
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
