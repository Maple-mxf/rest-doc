package restdoc.client.dubbo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
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
@Import(AgentConfiguration::class, DubboInvokerImpl::class,
        InvokeAPIHandler::class,
        ReportClientInfoHandler::class,
        ExportAPIHandler::class,
        DubboRefBeanManager::class)
@ConditionalOnClass(value = [AgentConfiguration::class])
open class DubboAgentClientConfiguration : AgentClientConfiguration {

    @Autowired
    lateinit var invokeAPIHandler: InvokeAPIHandler

    @Autowired
    lateinit var reportClientInfoHandler: ReportClientInfoHandler

    @Autowired
    lateinit var exportAPIHandler: ExportAPIHandler

    @Autowired
    lateinit var agentImpl: AgentImpl

    override fun getInvokeAPIHandler(): NettyRequestProcessor = this.invokeAPIHandler

    override fun getReportClientInfoHandler(): NettyRequestProcessor = this.reportClientInfoHandler

    override fun getExportAPIHandler(): NettyRequestProcessor = this.exportAPIHandler

    override fun getAgent(): Agent = this.agentImpl
}