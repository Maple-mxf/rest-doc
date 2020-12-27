package restdoc.client.restweb;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import restdoc.client.api.Agent;
import restdoc.client.api.AgentClientConfiguration;
import restdoc.client.api.AgentConfigurationProperties;
import restdoc.client.api.AgentImpl;
import restdoc.client.restweb.context.EndpointsListener;
import restdoc.client.restweb.handler.ExportApiHandler;
import restdoc.client.restweb.handler.InvokerApiHandler;
import restdoc.client.restweb.handler.ReportClientInfoHandler;
import restdoc.remoting.netty.NettyRequestProcessor;

/**
 * @author Maple
 */
@Configuration
@Import(value = {EnvConfiguration.class})
public class RestWebAgentClientConfiguration implements AgentClientConfiguration, PriorityOrdered {

    private final AgentImpl agentImpl;

    private final ReportClientInfoHandler reportClientInfoHandler;

    private final ExportApiHandler exportApiHandler;

    private final InvokerApiHandler invokerApiHandler;

    public RestWebAgentClientConfiguration(@Qualifier(value = "restWebAgentImpl") AgentImpl agentImpl,
                                           ReportClientInfoHandler reportClientInfoHandler,
                                           ExportApiHandler exportApiHandler,
                                           InvokerApiHandler invokerApiHandler) {
        this.agentImpl = agentImpl;
        this.reportClientInfoHandler = reportClientInfoHandler;
        this.exportApiHandler = exportApiHandler;
        this.invokerApiHandler = invokerApiHandler;
    }

    @Bean
    @ConditionalOnMissingBean
    public EndpointsListener endpointsListener(Environment environment) {
        return new EndpointsListener(environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public InvokerApiHandler invokerApiHandler(RestWebInvokerImpl invoker) {
        return new InvokerApiHandler(invoker);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestWebInvokerImpl restWebInvoker(Environment environment, RestTemplate restTemplate) {
        return new RestWebInvokerImpl(environment, restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(){return new RestTemplate();}

    @Bean
    @ConditionalOnMissingBean
    public ReportClientInfoHandler reportClientInfoHandler(AgentConfigurationProperties configurationProperties,
                                                           Environment environment) {
        return new ReportClientInfoHandler(configurationProperties, environment);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @NotNull
    @Override
    public NettyRequestProcessor getInvokeAPIHandler() {
        return this.invokerApiHandler;
    }

    @NotNull
    @Override
    public NettyRequestProcessor getReportClientInfoHandler() {
        return this.reportClientInfoHandler;
    }

    @NotNull
    @Override
    public NettyRequestProcessor getExportAPIHandler() {
        return this.exportApiHandler;
    }

    @NotNull
    @Override
    public Agent getAgent() {
        return this.agentImpl;
    }

   /* @Override
    public void registryRemotingTask() {

    }

    @Override
    public void registryRemotingHandler() {

    }*/

    @Override
    public void run(@NotNull String... args) {

        Agent agent = this.getAgent();

        // 1 registryRemotingTask
        this.registryRemotingTask();

        // 2 registryRemotingHandler
        this.registryRemotingHandler();

        // 3 start agent
        agent.start();
    }
}
