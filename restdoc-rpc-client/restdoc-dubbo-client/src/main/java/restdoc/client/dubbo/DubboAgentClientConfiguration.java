package restdoc.client.dubbo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import restdoc.client.api.*;
import restdoc.client.dubbo.handler.ExportApiHandler;
import restdoc.client.dubbo.handler.InvokeApiHandler;
import restdoc.client.dubbo.handler.ReportClientInfoHandler;
import restdoc.remoting.netty.NettyRequestProcessor;

/**
 * The class DubboAgentClientConfiguration
 * SPI interface
 *
 * @author Maple
 * @see java.util.ServiceLoader
 * @since 1.0.RELEASE
 */
@Configuration
@Import(value = {DubboInvokerImpl.class,
        InvokeApiHandler.class,
        ReportClientInfoHandler.class,
        ExportApiHandler.class,
        DubboRefBeanManager.class,
        EnvConfiguration.class})
@EnableConfigurationProperties(value = {AgentConfigurationProperties.class})
@SPI(name = "restdoc.client.dubbo.DubboAgentClientConfiguration")
public class DubboAgentClientConfiguration implements AgentClientConfiguration {

    @Autowired
    private InvokeApiHandler invokeAPIHandler;

    @Autowired
    private ReportClientInfoHandler reportClientInfoHandler;

    @Autowired
    private ExportApiHandler exportApiHandler;

    @Autowired
    @Qualifier(value = "dubboAgentImpl")
    private AgentImpl agentImpl;

    public DubboAgentClientConfiguration() {
    }

    @Override
    public NettyRequestProcessor getInvokeAPIHandler() {
        return this.invokeAPIHandler;
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

    @Override
    public String module() {
        return "Dubbo-client-module";
    }
}
