package restdoc.client.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import restdoc.remoting.protocol.RemotingSerializable;
import restdoc.rpc.client.common.model.ApplicationType;

import java.beans.ConstructorProperties;
import java.util.Objects;

@JsonPropertyOrder(value = {"osname", "hostname", "type", "service", "serializationProtocol"})
public class ClientInfo extends RemotingSerializable {

    private String osname;

    private String hostname;

    private ApplicationType type;

    private String service;

    private String serializationProtocol;

    @ConstructorProperties(value = {"osname", "hostname", "type", "service", "serializationProtocol"})
    public ClientInfo(String osname,
                      String hostname,
                      ApplicationType type,
                      String service,
                      String serializationProtocol) {
        this.osname = osname;
        this.hostname = hostname;
        this.type = type;
        this.service = service;
        this.serializationProtocol = serializationProtocol;
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

    public ApplicationType getType() {
        return type;
    }

    public void setType(ApplicationType type) {
        this.type = type;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSerializationProtocol() {
        return serializationProtocol;
    }

    public void setSerializationProtocol(String serializationProtocol) {
        this.serializationProtocol = serializationProtocol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientInfo that = (ClientInfo) o;
        return Objects.equals(osname, that.osname) &&
                Objects.equals(hostname, that.hostname) &&
                type == that.type &&
                Objects.equals(service, that.service) &&
                Objects.equals(serializationProtocol, that.serializationProtocol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(osname, hostname, type, service, serializationProtocol);
    }
}
