package restdoc.web.schedule;

import restdoc.rpc.client.common.model.ApplicationType;
import restdoc.rpc.client.common.model.ApiDescriptor;

import java.util.List;
import java.util.Objects;

@Deprecated
public class Api {

    private String service;

    private ApplicationType at;

    private List<ApiDescriptor> apiList;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public ApplicationType getAt() {
        return at;
    }

    public void setAt(ApplicationType at) {
        this.at = at;
    }

    public List<ApiDescriptor> getApiList() {
        return apiList;
    }

    public void setApiList(List<ApiDescriptor> apiList) {
        this.apiList = apiList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Api api = (Api) o;
        return Objects.equals(service, api.service) &&
                at == api.at &&
                Objects.equals(apiList, api.apiList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(service, at, apiList);
    }
}
