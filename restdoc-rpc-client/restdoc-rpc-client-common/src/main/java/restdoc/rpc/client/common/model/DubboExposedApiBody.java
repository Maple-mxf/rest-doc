package restdoc.rpc.client.common.model;

import restdoc.remoting.common.DubboApiDescriptor;

import java.util.List;

/**
 * ReportClientExportInterfacesBody
 */
@Deprecated
public class DubboExposedApiBody extends BaseExposedApiBody {

    private List<DubboApiDescriptor> apiList;

    public DubboExposedApiBody() {
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
