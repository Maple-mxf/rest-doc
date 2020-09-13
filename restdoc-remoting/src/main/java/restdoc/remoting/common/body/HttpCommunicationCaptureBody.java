package restdoc.remoting.common.body;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import restdoc.remoting.protocol.RemotingSerializable;

import java.util.List;
import java.util.Map;

/**
 * HttpCommunicationCaptureBody
 */
@Deprecated
public class HttpCommunicationCaptureBody extends RemotingSerializable {

    private int status;

    private HttpMethod method;

    private String url, completeUrl;

    private Map<String, List<String>> requestHeader;

    @Deprecated
    private Map<String, String> queryParam;

    private Map<String, Object> requestBody;

    private Map<String, String> uriVariables;

    private Map<String, List<String>> responseHeader;

    private Object responseBody;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public HttpMethod getMethod() {
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


    public void setRequestHeader(HttpHeaders requestHeader) {
        this.requestHeader = requestHeader;
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

    public void setRequestHeaders(MultiValueMap<String, String> requestHeaders) {
        this.requestHeader = requestHeaders;
    }

    public Map<String, List<String>> getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeaders(Map<String, List<String>> requestHeaders) {
        this.requestHeader = requestHeaders;
    }

    public Map<String, List<String>> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(Map<String, List<String>> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public void setResponseHeader(MultiValueMap<String, String> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public void setRequestHeader(Map<String, List<String>> requestHeader) {
        this.requestHeader = requestHeader;
    }
}
