package restdoc.client.api.model;

import restdoc.remoting.protocol.RemotingSerializable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RestWebInvocation extends RemotingSerializable implements Invocation{

    private String url;

    private String method;

    private Map<String, List<String>> requestHeaders;

    private Map<String, Object> queryParam;

    private Map<String, Object> requestBody;

    private Map<String, Object> uriVariable;

    public RestWebInvocation() {
    }

    public RestWebInvocation(String url, String method, Map<String, List<String>> requestHeaders, Map<String, Object> queryParam, Map<String, Object> requestBody, Map<String, Object> uriVariable) {
        this.url = url;
        this.method = method;
        this.requestHeaders = requestHeaders;
        this.queryParam = queryParam;
        this.requestBody = requestBody;
        this.uriVariable = uriVariable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, List<String>> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, List<String>> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Map<String, Object> getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(Map<String, Object> queryParam) {
        this.queryParam = queryParam;
    }

    public Map<String, Object> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Map<String, Object> requestBody) {
        this.requestBody = requestBody;
    }

    public Map<String, Object> getUriVariable() {
        return uriVariable;
    }

    public void setUriVariable(Map<String, Object> uriVariable) {
        this.uriVariable = uriVariable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestWebInvocation that = (RestWebInvocation) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(method, that.method) &&
                Objects.equals(requestHeaders, that.requestHeaders) &&
                Objects.equals(queryParam, that.queryParam) &&
                Objects.equals(requestBody, that.requestBody) &&
                Objects.equals(uriVariable, that.uriVariable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method, requestHeaders, queryParam, requestBody, uriVariable);
    }
}
