package restdoc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import restdoc.client.remoting.ApplicationClient;
import restdoc.client.remoting.TaskChannelInboundHandlerAdapter;
import restdoc.remoting.netty.RemotingCommandDecoder;
import restdoc.remoting.netty.RemotingCommandEncoder;

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
        new Thread(client::connection).start();
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
