package restdoc.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import restdoc.client.context.EndpointsListener;
import restdoc.client.invoke.HttpInvoker;
import restdoc.client.remoting.HttpApplicationAgent;
import restdoc.client.remoting.HttpTaskRequestProcessor;
import restdoc.client.remoting.PostEmptyApiTemplateRequestProcessor;

@Configuration
@EnableConfigurationProperties(RestDocProperties.class)
public class RestDocClientConfiguration {

    private static Logger log = LoggerFactory.getLogger(RestDocClientConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public HttpApplicationAgent applicationClient(RestDocProperties restDocProperties,
                                                  HttpTaskRequestProcessor httpTaskRequestProcessor,
                                                  PostEmptyApiTemplateRequestProcessor postEmptyApiTemplateRequestProcessor
                                               ) {
        HttpApplicationAgent httpApplicationAgent = new HttpApplicationAgent(
                restDocProperties,
                httpTaskRequestProcessor,
                postEmptyApiTemplateRequestProcessor);

        httpApplicationAgent.connection();
        return httpApplicationAgent;
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpInvoker httpTaskExecutor(RestTemplate restTemplate, Environment environment) {
        return new HttpInvoker(restTemplate, environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpTaskRequestProcessor httpTaskRequestProcessor(HttpInvoker httpInvoker) {
        return new HttpTaskRequestProcessor(httpInvoker);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public EndpointsListener endpointsListener(Environment environment) {
        return new EndpointsListener(environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public PostEmptyApiTemplateRequestProcessor postEmptyApiTemplateRequestProcessor(EndpointsListener endpointsListener) {
        return new PostEmptyApiTemplateRequestProcessor(endpointsListener);
    }
}
