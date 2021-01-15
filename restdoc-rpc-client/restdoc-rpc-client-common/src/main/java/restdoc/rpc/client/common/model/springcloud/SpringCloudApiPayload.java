package restdoc.rpc.client.common.model.springcloud;

import restdoc.rpc.client.common.model.ApplicationType;
import restdoc.rpc.client.common.model.AbstractApiPayload;

import java.util.List;

@Deprecated
public class SpringCloudApiPayload extends AbstractApiPayload {

    private List<SpringCloudApiDescriptor> apiList;

    public SpringCloudApiPayload() {
        super(ApplicationType.SPRINGCLOUD);
    }

    public List<SpringCloudApiDescriptor> getApiList() {
        return apiList;
    }

    public void setApiList(List<SpringCloudApiDescriptor> apiList) {
        this.apiList = apiList;
    }
}
