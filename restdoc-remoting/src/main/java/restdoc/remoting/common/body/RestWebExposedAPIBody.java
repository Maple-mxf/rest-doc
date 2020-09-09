package restdoc.remoting.common.body;

import restdoc.remoting.common.RestWebAPI;

import java.util.List;

public class RestWebExposedAPIBody extends BaseExposedAPIBody {

    private List<RestWebAPI> apiList;

    public void setApiList(List<RestWebAPI> apiList) {
        this.apiList = apiList;
    }

    public List<RestWebAPI> getApiList() {
        return apiList;
    }

}
