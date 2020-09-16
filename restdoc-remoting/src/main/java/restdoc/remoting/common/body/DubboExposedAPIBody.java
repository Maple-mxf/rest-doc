package restdoc.remoting.common.body;

import restdoc.remoting.common.ApplicationType;
import restdoc.remoting.common.DubboExposedAPI;

import java.util.List;

/**
 * ReportClientExportInterfacesBody
 */
@Deprecated
public class DubboExposedAPIBody extends BaseExposedAPIBody {

    private List<DubboExposedAPI> apiList;

    public DubboExposedAPIBody() {
        super(ApplicationType.DUBBO);
    }

    public List<DubboExposedAPI> getApiList() {
        return apiList;
    }

    public void setApiList(List<DubboExposedAPI> apiList) {
        this.apiList = apiList;
    }

    @Override
    public String toString() {
        return "ReportClientExportInterfacesBody[" +
                "exportInterfaces=" + apiList +
                ']';
    }


}
