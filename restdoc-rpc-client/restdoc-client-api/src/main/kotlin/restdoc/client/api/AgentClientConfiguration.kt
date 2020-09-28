package restdoc.client.api

import org.springframework.boot.CommandLineRunner
import restdoc.remoting.common.RequestCode
import restdoc.remoting.netty.NettyRequestProcessor


/**
 * AgentClientConfiguration
 */
interface AgentClientConfiguration : CommandLineRunner {

    /**
     * Registry RemotingTask
     */
    fun registryRemotingTask(){}

    /**
     * Request process handelr
     */
    fun registryRemotingHandler() {
        getAgent().addHandler(RequestCode.REPORT_CLIENT_INFO, getReportClientInfoHandler())
        getAgent().addHandler(RequestCode.REPORT_EXPOSED_API, getExportAPIHandler())
        getAgent().addHandler(RequestCode.INVOKE_API, getInvokeAPIHandler())
    }

    /**
     *
     */
    fun getInvokeAPIHandler(): NettyRequestProcessor

    /**
     *
     */
    fun getReportClientInfoHandler(): NettyRequestProcessor

    /**
     *
     */
    fun getExportAPIHandler(): NettyRequestProcessor

    /**
     * Start The Agent client
     */
    override fun run(vararg args: String) {

        val agent = this.getAgent()

        // 1 registryRemotingTask
        this.registryRemotingTask()

        // 2 registryRemotingHandler
        this.registryRemotingHandler()

        // 3 start agent
        agent.start()
    }

    fun getAgent(): Agent

}