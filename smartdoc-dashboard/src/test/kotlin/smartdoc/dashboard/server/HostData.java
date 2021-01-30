package smartdoc.dashboard.server;

import restdoc.remoting.protocol.RemotingSerializable;

@Deprecated
public class HostData extends RemotingSerializable {

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
