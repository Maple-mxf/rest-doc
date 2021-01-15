package restdoc.rpc.client.common.model;


import restdoc.rpc.client.common.model.dubbo.DubboApiDescriptor;

import java.util.List;

/**
 * ReportClientExportInterfacesBody
 */
public class DubboApiPayload extends AbstractApiPayload {

    private List<DubboApiDescriptor> apiList;

    public DubboApiPayload() {
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
