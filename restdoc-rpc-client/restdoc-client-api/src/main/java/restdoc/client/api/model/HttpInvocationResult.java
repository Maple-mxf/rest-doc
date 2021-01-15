package restdoc.client.api.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 */
public class HttpInvocationResult extends InvocationResult {

    private int status = 200;
    private Map<String, List<String>> responseHeaders = new HashMap<>();
    private Object responseBody = null;

    public HttpInvocationResult() {
    }

    public HttpInvocationResult(Boolean isSuccessful,
                                String exceptionMsg,
                                Invocation invocation,
                                int status,
                                Map<String, List<String>> responseHeaders,
                                Object responseBody
    ) {
        super(isSuccessful, exceptionMsg, invocation);
        this.status = status;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpInvocationResult that = (HttpInvocationResult) o;
        return status == that.status &&
                Objects.equals(responseHeaders, that.responseHeaders) &&
                Objects.equals(responseBody, that.responseBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, responseHeaders, responseBody);
    }
}
