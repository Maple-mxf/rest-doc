package restdoc.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import restdoc.client.remoting.ApplicationClient;

@Configuration
@EnableConfigurationProperties(RestDocProperties.class)
public class RestDocClientConfiguration {

    private final RestDocProperties restDocProperties;

    public RestDocClientConfiguration(RestDocProperties restDocProperties) {
        this.restDocProperties = restDocProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationClient applicationClient() {
        ApplicationClient client = new ApplicationClient(this.restDocProperties);
        client.connection();
        return client;
    }
}
