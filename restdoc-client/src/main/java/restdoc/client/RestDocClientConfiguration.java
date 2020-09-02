package restdoc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import restdoc.client.remoting.ApplicationClient;
import restdoc.client.remoting.RemotingCommandDecoder;
import restdoc.client.remoting.RemotingCommandEncoder;
import restdoc.client.remoting.TaskChannelInboundHandlerAdapter;

@Configuration
@EnableConfigurationProperties(RestDocProperties.class)
public class RestDocClientConfiguration {

    private final RestDocProperties restDocProperties;

    private static Logger log = LoggerFactory.getLogger(RestDocClientConfiguration.class);

    public RestDocClientConfiguration(RestDocProperties restDocProperties) {
        this.restDocProperties = restDocProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationClient applicationClient(TaskChannelInboundHandlerAdapter taskChannelInboundHandlerAdapter,
                                               RemotingCommandDecoder remotingCommandDecoder,
                                               RemotingCommandEncoder remotingCommandEncoder) {
        ApplicationClient client = new ApplicationClient(
                this.restDocProperties,
                taskChannelInboundHandlerAdapter,
                remotingCommandEncoder,
                remotingCommandDecoder);

        client.connection();
        return client;
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskChannelInboundHandlerAdapter taskChannelInboundHandlerAdapter() {
        return new TaskChannelInboundHandlerAdapter();
    }

    @Bean
    @ConditionalOnMissingBean
    public RemotingCommandDecoder remotingCommandDecoder() {
        return new RemotingCommandDecoder();
    }

    @Bean
    @ConditionalOnMissingBean
    public RemotingCommandEncoder remotingCommandEncoder() {
        return new RemotingCommandEncoder();
    }
}
