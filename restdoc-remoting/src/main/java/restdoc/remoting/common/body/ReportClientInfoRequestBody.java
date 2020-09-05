package restdoc.remoting.common.body;

import restdoc.remoting.protocol.RemotingSerializable;

public class ReportClientInfoRequestBody extends RemotingSerializable {
    private String osname;

    private String service;

    private String hostname;

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
