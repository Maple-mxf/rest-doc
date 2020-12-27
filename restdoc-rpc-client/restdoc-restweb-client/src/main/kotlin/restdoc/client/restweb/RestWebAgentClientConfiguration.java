package restdoc.client.restweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.PriorityOrdered;
import restdoc.client.api.Agent;
import restdoc.client.api.AgentClientConfiguration;
import restdoc.client.api.AgentImpl;
import restdoc.client.restweb.handler.ExportApiHandler;
import restdoc.client.restweb.handler.InvokerApiHandler;
import restdoc.client.restweb.handler.ReportClientInfoHandler;
import restdoc.remoting.netty.NettyRequestProcessor;

/**
 * The class RestWebAgentClientConfiguration
 *
 * @author Maple
 */
@Configuration
@Import(value = {EnvConfiguration.class})
@AutoConfigureBefore(value = {EnvConfiguration.class})
public class RestWebAgentClientConfiguration implements AgentClientConfiguration, PriorityOrdered {

    @Autowired
    private AgentImpl agentImpl;

    @Autowired
    private ReportClientInfoHandler reportClientInfoHandler;

    @Autowired
    private ExportApiHandler exportApiHandler;

    @Autowired
    private InvokerApiHandler invokerApiHandler;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public NettyRequestProcessor getInvokeAPIHandler() {
        return this.invokerApiHandler;
    }

    @Override
    public NettyRequestProcessor getReportClientInfoHandler() {
        return this.reportClientInfoHandler;
    }

    @Override
    public NettyRequestProcessor getExportAPIHandler() {
        return this.exportApiHandler;
    }

    @Override
    public Agent getAgent() {
        return this.agentImpl;
    }

}
