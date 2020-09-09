package restdoc.remoting.common.body;

import restdoc.remoting.common.SpringCloudAPI;

import java.util.List;

public class SpringCloudExposeAPIBody extends BaseExposedAPIBody {

    private List<SpringCloudAPI> apiList;

    public List<SpringCloudAPI> getApiList() {
        return apiList;
    }

    public void setApiList(List<SpringCloudAPI> apiList) {
        this.apiList = apiList;
    }
}
