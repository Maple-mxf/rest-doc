package restdoc.remoting.common.body;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import restdoc.remoting.protocol.RemotingSerializable;

import java.util.Map;

public class HttpCommunicationCapture extends RemotingSerializable {

    private int status;

    private HttpMethod method;

    private String url, completeUrl;

    private HttpHeaders requestHeaders;

    private Map<String,String> queryParam;

    private Map<String, Object> requestBody;

    private Map<String, String> uriVariables;

    private HttpHeaders responseHeader;

    private Object responseBody;

    private MediaType responseContentType;

    public MediaType getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(MediaType responseContentType) {
        this.responseContentType = responseContentType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public HttpMethod  getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCompleteUrl() {
        return completeUrl;
    }

    public void setCompleteUrl(String completeUrl) {
        this.completeUrl = completeUrl;
    }

    public HttpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(HttpHeaders requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Map<String, Object> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Map<String, Object> requestBody) {
        this.requestBody = requestBody;
    }

    public Map<String, String> getUriVariables() {
        return uriVariables;
    }

    public void setUriVariables(Map<String, String> uriVariables) {
        this.uriVariables = uriVariables;
    }

    public HttpHeaders getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(HttpHeaders responseHeader) {
        this.responseHeader = responseHeader;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, String> getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(Map<String, String> queryParam) {
        this.queryParam = queryParam;
    }
}
