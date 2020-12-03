package restdoc.remoting.common.body;

import restdoc.remoting.common.ApplicationType;
import restdoc.remoting.common.SpringCloudApiDescriptor;

import java.util.List;

@Deprecated
public class SpringCloudExposeAPIBody extends BaseExposedAPIBody {

    private List<SpringCloudApiDescriptor> apiList;

    public SpringCloudExposeAPIBody() {
        super(ApplicationType.SPRINGCLOUD);
    }

    public List<SpringCloudApiDescriptor> getApiList() {
        return apiList;
    }

    public void setApiList(List<SpringCloudApiDescriptor> apiList) {
        this.apiList = apiList;
    }
}
