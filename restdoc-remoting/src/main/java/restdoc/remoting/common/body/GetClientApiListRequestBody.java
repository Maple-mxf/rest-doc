package restdoc.remoting.common.body;

import restdoc.remoting.data.ApiEmptyTemplate;
import restdoc.remoting.protocol.RemotingSerializable;

import java.util.List;

/**
 * The GetClientApiListRequestBody Empty
 *
 * @author ubuntu-m
 */
public class GetClientApiListRequestBody extends RemotingSerializable {

    private List<ApiEmptyTemplate> apiEmptyTemplates;

    public List<ApiEmptyTemplate> getApiEmptyTemplates() {
        return apiEmptyTemplates;
    }

    public void setApiEmptyTemplates(List<ApiEmptyTemplate> apiEmptyTemplates) {
        this.apiEmptyTemplates = apiEmptyTemplates;
    }
}
