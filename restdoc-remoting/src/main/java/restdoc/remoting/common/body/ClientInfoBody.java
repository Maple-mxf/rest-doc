package restdoc.remoting.common.body;

import restdoc.remoting.common.ApplicationType;
import restdoc.remoting.protocol.RemotingSerializable;

public class ClientInfoBody extends RemotingSerializable {
    private String osname;

    private String service;

    private String hostname;

    private ApplicationType applicationType;

    private String serializationProtocol;

    public String getSerializationProtocol() {
        return serializationProtocol;
    }

    public void setSerializationProtocol(String serializationProtocol) {
        this.serializationProtocol = serializationProtocol;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getOsname() {
        return osname;
    }

    public void setOsname(String osname) {
        this.osname = osname;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

}
