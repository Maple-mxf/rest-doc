package restdoc.remoting.common.body;

import restdoc.remoting.annotation.Nullable;
import restdoc.remoting.protocol.RemotingSerializable;

import java.util.Map;

public class SubmitHttpTaskRequestBody extends RemotingSerializable {

    private String url;

    private String method;

    private Map<String, String> header;

    @Nullable
    private Map<String, String> uriVar;

    @Nullable
    private Map<String, Object> body;

    @Nullable
    private Map<String, Object> queryParam;

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

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public Map<String, String> getUriVar() {
        return uriVar;
    }

    public void setUriVar(Map<String, String> uriVar) {
        this.uriVar = uriVar;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public Map<String, Object> getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(Map<String, Object> queryParam) {
        this.queryParam = queryParam;
    }
}
