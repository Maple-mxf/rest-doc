package restdoc.rpc.client.common.model.springcloud;

import restdoc.rpc.client.common.model.ApplicationType;
import restdoc.rpc.client.common.model.BaseExposedApiBody;

import java.util.List;

@Deprecated
public class SpringCloudExposeApiBody extends BaseExposedApiBody {

    private List<SpringCloudApiDescriptor> apiList;

    public SpringCloudExposeApiBody() {
        super(ApplicationType.SPRINGCLOUD);
    }

    public List<SpringCloudApiDescriptor> getApiList() {
        return apiList;
    }

    public void setApiList(List<SpringCloudApiDescriptor> apiList) {
        this.apiList = apiList;
    }
}
