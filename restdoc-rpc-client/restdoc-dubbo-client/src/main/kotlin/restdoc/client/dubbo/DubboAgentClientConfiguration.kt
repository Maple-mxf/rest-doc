package restdoc.client.dubbo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import restdoc.client.api.*
import restdoc.client.dubbo.handler.ExportAPIHandler
import restdoc.client.dubbo.handler.InvokeAPIHandler
import restdoc.client.dubbo.handler.ReportClientInfoHandler
import restdoc.remoting.netty.NettyRequestProcessor


/**
 * DubboAgentClientConfiguration
 */
@Configuration
@Import(DubboInvokerImpl::class,
        InvokeAPIHandler::class,
        ReportClientInfoHandler::class,
        ExportAPIHandler::class,
        DubboRefBeanManager::class,
        EnvConfiguration::class)
@EnableConfigurationProperties(value = [AgentConfigurationProperties::class])
//@ConditionalOnClass(value = [EnvConfiguration::class])
open class DubboAgentClientConfiguration : AgentClientConfiguration {

    @Autowired
    lateinit var invokeAPIHandler: InvokeAPIHandler

    @Autowired
    lateinit var reportClientInfoHandler: ReportClientInfoHandler

    @Autowired
    lateinit var exportAPIHandler: ExportAPIHandler

    @Autowired
    @Qualifier(value = "dubboAgentImpl")
    lateinit var agentImpl: AgentImpl

    override fun getInvokeAPIHandler(): NettyRequestProcessor = this.invokeAPIHandler

    override fun getReportClientInfoHandler(): NettyRequestProcessor = this.reportClientInfoHandler

    override fun getExportAPIHandler(): NettyRequestProcessor = this.exportAPIHandler

    override fun getAgent(): Agent = this.agentImpl
}