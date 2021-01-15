package restdoc.rpc.client.common.model.http;

import restdoc.rpc.client.common.model.AbstractApiPayload;
import restdoc.rpc.client.common.model.ApplicationType;

import java.util.List;

public class HttpApiPayload extends AbstractApiPayload {

    private List<HttpApiDescriptor> apiList;

    public HttpApiPayload() {
        super(ApplicationType.REST_WEB);
    }

    public void setApiList(List<HttpApiDescriptor> apiList) {
        this.apiList = apiList;
    }

    public List<HttpApiDescriptor> getApiList() {
        return apiList;
    }

}
