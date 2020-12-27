package restdoc.client.dubbo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import restdoc.client.api.Agent;
import restdoc.client.api.AgentClientConfiguration;
import restdoc.client.api.AgentConfigurationProperties;
import restdoc.client.api.AgentImpl;
import restdoc.client.dubbo.handler.ExportApiHandler;
import restdoc.client.dubbo.handler.InvokeApiHandler;
import restdoc.client.dubbo.handler.ReportClientInfoHandler;
import restdoc.remoting.netty.NettyRequestProcessor;

/**
 * DubboAgentClientConfiguration
 *
 * @author Maple
 */
@Configuration
@Import(value = {DubboInvokerImpl.class,
        InvokeApiHandler.class,
        ReportClientInfoHandler.class,
        ExportApiHandler.class,
        DubboRefBeanManager.class,
        EnvConfiguration.class})
@EnableConfigurationProperties(value ={AgentConfigurationProperties.class})
//@ConditionalOnClass(value = [EnvConfiguration::class])
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

    @Override
    public NettyRequestProcessor getInvokeAPIHandler()  {return this.invokeAPIHandler;}
    @Override
    public NettyRequestProcessor getReportClientInfoHandler(){return  this.reportClientInfoHandler;}
    @Override
    public NettyRequestProcessor getExportAPIHandler(){return this.exportApiHandler;}
    @Override
    public Agent getAgent(){return this.agentImpl;}
}
