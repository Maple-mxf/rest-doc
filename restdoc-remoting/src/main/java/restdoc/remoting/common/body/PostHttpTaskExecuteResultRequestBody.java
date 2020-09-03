package restdoc.remoting.common.body;

import restdoc.remoting.protocol.RemotingSerializable;

import java.util.Map;

/**
 * @author ubuntu-m
 */
public class PostHttpTaskExecuteResultRequestBody extends RemotingSerializable {

    private int status;

    private Map<String, String> responseHeader;

    private Object responseBody;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, String> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(Map<String, String> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }
}
