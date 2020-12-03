package restdoc.remoting.common.body;

import restdoc.remoting.common.ApplicationType;
import restdoc.remoting.common.ApiDescriptor;
import restdoc.remoting.protocol.RemotingSerializable;

import java.util.List;

public abstract class BaseExposedAPIBody extends RemotingSerializable {

    private ApplicationType applicationType;

    public BaseExposedAPIBody(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    /**
     * @return api list
     */
    public abstract List<? extends ApiDescriptor> getApiList();

    /**
     * Application service name
     */
    public String service;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }
}
