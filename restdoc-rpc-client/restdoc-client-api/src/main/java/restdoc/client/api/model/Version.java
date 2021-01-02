package restdoc.client.api.model;

import restdoc.remoting.protocol.RemotingSerializable;

/**
 * The class Version
 *
 * @author Maple
 * @since 2.0.RELEASE
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
