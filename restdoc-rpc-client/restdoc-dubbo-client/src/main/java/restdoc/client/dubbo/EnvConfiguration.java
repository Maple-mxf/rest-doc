package restdoc.client.dubbo;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import restdoc.client.api.AgentImpl;
import restdoc.client.api.ServerProperties;

@EnableConfigurationProperties(value = {AgentConfigurationProperties.class})
@Configuration
public class EnvConfiguration {

    @Bean(name = "dubboAgentImpl")
    AgentImpl agentImpl(AgentConfigurationProperties properties) {
        return new AgentImpl(new ServerProperties() {
            @Override
            public String host() {
                return properties.getHost();
            }
            @Override
            public int port() {
                return properties.getPort();
            }
            @Override
            public String service() {
                return properties.getService();
            }
        });
    }
}
