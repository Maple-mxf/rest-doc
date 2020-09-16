package restdoc.remoting.common.body;

import restdoc.remoting.common.ApplicationType;
import restdoc.remoting.common.RestWebExposedAPI;

import java.util.List;

@Deprecated
public class RestWebExposedAPIBody extends BaseExposedAPIBody {

    private List<RestWebExposedAPI> apiList;

    public RestWebExposedAPIBody() {
        super(ApplicationType.REST_WEB);
    }

    public void setApiList(List<RestWebExposedAPI> apiList) {
        this.apiList = apiList;
    }

    public List<RestWebExposedAPI> getApiList() {
        return apiList;
    }

}
