package restdoc.client.restweb

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.client.RestTemplate
import restdoc.client.api.*
import restdoc.client.restweb.context.EndpointsListener
import restdoc.client.restweb.handler.ExportAPIHandler
import restdoc.client.restweb.handler.InvokerAPIHandler
import restdoc.client.restweb.handler.ReportClientInfoHandler
import restdoc.remoting.netty.NettyRequestProcessor

/**
 * @author Overman
 */
@Configuration
@Import(value = [AgentConfiguration::class, EndpointsListener::class,
    InvokerAPIHandler::class, ReportClientInfoHandler::class, ExportAPIHandler::class])
@ConditionalOnClass(value = [AgentConfiguration::class])
open class RestWebAgentClientConfiguration : AgentClientConfiguration {

    @Autowired
    lateinit var agentImpl: AgentImpl

    @Autowired
    lateinit var invokerAPIHandler: InvokerAPIHandler

    @Autowired
    lateinit var reportClientInfoHandler: ReportClientInfoHandler

    @Autowired
    lateinit var exportAPIHandler: ExportAPIHandler

    @Bean
    @ConditionalOnMissingBean
    open fun restTemplate() = RestTemplate()

    override fun getInvokeAPIHandler(): NettyRequestProcessor = this.invokerAPIHandler

    override fun getReportClientInfoHandler(): NettyRequestProcessor = this.reportClientInfoHandler

    override fun getExportAPIHandler(): NettyRequestProcessor = this.exportAPIHandler

    override fun getAgent() = this.agentImpl
}