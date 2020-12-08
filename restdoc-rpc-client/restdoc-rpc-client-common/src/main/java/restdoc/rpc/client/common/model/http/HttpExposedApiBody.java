package restdoc.rpc.client.common.model.http;

import restdoc.rpc.client.common.model.ApplicationType;
import restdoc.rpc.client.common.model.BaseExposedApiBody;

import java.util.List;

public class HttpExposedApiBody extends BaseExposedApiBody {

    private List<RestWebApiDescriptor> apiList;

    public HttpExposedApiBody() {
        super(ApplicationType.REST_WEB);
    }

    public void setApiList(List<RestWebApiDescriptor> apiList) {
        this.apiList = apiList;
    }

    public List<RestWebApiDescriptor> getApiList() {
        return apiList;
    }

}
