package restdoc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import restdoc.client.executor.HttpTaskExecutor;
import restdoc.client.remoting.ApplicationClient;
import restdoc.client.remoting.HttpTaskRequestProcessor;

@Configuration
@EnableConfigurationProperties(RestDocProperties.class)
public class RestDocClientConfiguration {

    private static Logger log = LoggerFactory.getLogger(RestDocClientConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public ApplicationClient applicationClient(RestDocProperties restDocProperties,
                                               HttpTaskRequestProcessor httpTaskRequestProcessor) {
        ApplicationClient applicationClient = new ApplicationClient(restDocProperties, httpTaskRequestProcessor);
        applicationClient.connection();
        return applicationClient;
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpTaskExecutor httpTaskExecutor(RestTemplate restTemplate, Environment environment) {
        return new HttpTaskExecutor(restTemplate, environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpTaskRequestProcessor httpTaskRequestProcessor(HttpTaskExecutor httpTaskExecutor) {
        return new HttpTaskRequestProcessor(httpTaskExecutor);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public EndpointsListener endpointsListener() {
        return new EndpointsListener();
    }
}
