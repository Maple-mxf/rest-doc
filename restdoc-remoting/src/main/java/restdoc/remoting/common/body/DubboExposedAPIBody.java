package restdoc.remoting.common.body;

import restdoc.remoting.common.DubboAPI;

import java.util.List;

/**
 * ReportClientExportInterfacesBody
 */
public class DubboExposedAPIBody extends BaseExposedAPIBody {

    private List<DubboAPI> apiList;

    public List<DubboAPI> getApiList() {
        return apiList;
    }

    public void setApiList(List<DubboAPI> apiList) {
        this.apiList = apiList;
    }

    @Override
    public String toString() {
        return "ReportClientExportInterfacesBody[" +
                "exportInterfaces=" + apiList +
                ']';
    }


}
