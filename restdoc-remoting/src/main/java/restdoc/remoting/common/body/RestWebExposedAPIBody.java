package restdoc.remoting.common.body;

import restdoc.remoting.common.ApplicationType;
import restdoc.rpc.client.common.model.RestWebApiDescriptor;

import java.util.List;

@Deprecated
public class RestWebExposedAPIBody extends BaseExposedAPIBody {

    private List<RestWebApiDescriptor> apiList;

    public RestWebExposedAPIBody() {
        super(ApplicationType.REST_WEB);
    }

    public void setApiList(List<RestWebApiDescriptor> apiList) {
        this.apiList = apiList;
    }

    public List<RestWebApiDescriptor> getApiList() {
        return apiList;
    }

}
