package restdoc.client.api.model;

import restdoc.remoting.protocol.RemotingSerializable;

/**
 * @author Maple
 */
public class Version extends RemotingSerializable {

    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
