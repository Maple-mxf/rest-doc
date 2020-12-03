package restdoc.remoting.common.body;

import restdoc.remoting.common.ApplicationType;
import restdoc.remoting.common.DubboApiDescriptor;

import java.util.List;

/**
 * ReportClientExportInterfacesBody
 */
@Deprecated
public class DubboExposedAPIBody extends BaseExposedAPIBody {

    private List<DubboApiDescriptor> apiList;

    public DubboExposedAPIBody() {
        super(ApplicationType.DUBBO);
    }

    public List<DubboApiDescriptor> getApiList() {
        return apiList;
    }

    public void setApiList(List<DubboApiDescriptor> apiList) {
        this.apiList = apiList;
    }

    @Override
    public String toString() {
        return "ReportClientExportInterfacesBody[" +
                "exportInterfaces=" + apiList +
                ']';
    }


}
