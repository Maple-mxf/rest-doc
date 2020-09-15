package restdoc.client.restweb

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import restdoc.client.api.*
import restdoc.client.restweb.context.EndpointsListener
import restdoc.remoting.InvokeCallback
import restdoc.remoting.common.ApplicationType
import restdoc.remoting.common.RemotingUtil
import restdoc.remoting.common.RequestCode
import restdoc.remoting.common.body.ClientInfoBody
import restdoc.remoting.common.body.RestWebExposedAPIBody
import restdoc.remoting.protocol.RemotingCommand

/**
 * @author Overman
 * @since 2020/9/15
 */
@Configuration
@Import(AgentConfiguration::class)
@ConditionalOnClass(value = [AgentConfiguration::class, EnvConfiguration::class])
open class RestWebAgentClientConfiguration : AgentClientConfiguration {

    @Autowired
    lateinit var agentConfigurationProperties: AgentConfigurationProperties

    @Autowired
    lateinit var agentImpl: AgentImpl

    @Autowired
    lateinit var endpointsListener: EndpointsListener

    override fun getAgent() = this.agentImpl

    /**
     *
     */
    override fun hook(): AgentHook {
        return object : AgentHook {
            override fun beforeStart() = listOf<AgentCallback>()
            override fun afterStart() = listOf<AgentCallback>()
        }
    }

    /**
     * registryRemotingTask
     */
    override fun registryRemotingTask() {
        // 1 Generate report client info interfaces
        agentImpl.addTask(reportClientInfoTask())

        // 2 Generate report http application exposed interfaces
        agentImpl.addTask(reportExposedInterfacesTask())
    }

    /**
     * Request process handelr
     */
    override fun registryRemotingHandler() {
    }


    private fun reportClientInfoTask(): RemotingTask {
        val serializationProtocol = "http"

        val body = ClientInfoBody()
        body.osname = System.getProperty("os.name")
        body.hostname = RemotingUtil.getHostname()
        body.service = agentConfigurationProperties.service
        body.applicationType = ApplicationType.DUBBO
        body.serializationProtocol = serializationProtocol

        val request = RemotingCommand.createRequestCommand(RequestCode.REPORT_CLIENT_INFO, null)
        request.body = body.encode()

        return RemotingTask(
                taskId = reportClientInfoTask,
                type = RemotingTaskType.SYNC,
                request = request,
                timeoutMills = 1000000L,
                invokeCallback = InvokeCallback { })
    }

    private fun reportExposedInterfacesTask(): RemotingTask {
        val body = RestWebExposedAPIBody()
        body.apiList = endpointsListener.restWebExposedAPIList
        val request = RemotingCommand.createRequestCommand(RequestCode.REPORT_EXPOSED_API, null)

        return RemotingTask(
                taskId = reportExposedInterfacesTask,
                type = RemotingTaskType.SYNC,
                request = request,
                timeoutMills = 1000000L,
                invokeCallback = InvokeCallback { })
    }
}