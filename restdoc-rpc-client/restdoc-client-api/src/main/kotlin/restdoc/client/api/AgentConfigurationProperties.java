package restdoc.client.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "restdoc")
public class AgentConfigurationProperties {

    private String host;

    private int port;

    private String service;

    public AgentConfigurationProperties() {
    }

    public AgentConfigurationProperties(String host, int port, String service) {
        this.host = host;
        this.port = port;
        this.service = service;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
