package restdoc.remoting.common.body;

import restdoc.remoting.common.RestWebExposedAPI;

import java.util.List;

public class RestWebExposedAPIBody extends BaseExposedAPIBody {

    private List<RestWebExposedAPI> apiList;

    public void setApiList(List<RestWebExposedAPI> apiList) {
        this.apiList = apiList;
    }

    public List<RestWebExposedAPI> getApiList() {
        return apiList;
    }

}
