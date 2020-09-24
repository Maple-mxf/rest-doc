package restdoc.client.dubbo

import org.apache.dubbo.config.ProtocolConfig
import org.apache.dubbo.config.context.ConfigManager
import org.apache.dubbo.config.spring.ServiceBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.util.CollectionUtils
import restdoc.client.api.*
import restdoc.client.api.model.ClientInfo
import restdoc.client.dubbo.handler.DubboInvokerAPIHandler
import restdoc.client.dubbo.model.ServiceDescriptor
import restdoc.remoting.InvokeCallback
import restdoc.remoting.common.ApplicationType
import restdoc.remoting.common.DubboExposedAPI
import restdoc.remoting.common.RemotingUtil
import restdoc.remoting.common.RequestCode
import restdoc.remoting.common.body.DubboExposedAPIBody
import restdoc.remoting.protocol.RemotingCommand


/**
 * DubboAgentClientConfiguration
 */
@Configuration
@Import(AgentConfiguration::class, DubboInvokerImpl::class, DubboInvokerAPIHandler::class, DubboRefBeanManager::class)
@ConditionalOnClass(value = [AgentConfiguration::class])
open class DubboAgentClientConfiguration : AgentClientConfiguration {

    @Autowired
    lateinit var beanFactory: ConfigurableListableBeanFactory

    @Autowired
    lateinit var agentConfigurationProperties: AgentConfigurationProperties

    @Autowired
    lateinit var dubboInvokerAPIHandler: DubboInvokerAPIHandler

    @Autowired
    lateinit var dubboRefBeanManager: DubboRefBeanManager

    @Autowired
    lateinit var agentImpl: AgentImpl


    private fun reportClientInfoTask(): RemotingTask {
        var serializationProtocol = "dubbo"

        try {
            val beansOfType = beanFactory.getBeansOfType(ServiceBean::class.java)
            if (!CollectionUtils.isEmpty(beansOfType)) {
                val protocol: ProtocolConfig? = beansOfType.entries.first().value.protocol
                if (protocol != null) serializationProtocol = protocol.name
            }
        } catch (ignore: Throwable) {
        }

        val body = ClientInfo(
                osname = System.getProperty("os.name", "Windows 10"),
                hostname = RemotingUtil.getHostname(),
                service = ConfigManager.getInstance().application.map { it.name }.orElse(agentConfigurationProperties.service),
                type = ApplicationType.DUBBO,
                serializationProtocol = serializationProtocol)

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
        val body = DubboExposedAPIBody()

        val beansOfType = beanFactory.getBeansOfType(ServiceBean::class.java)
        body.apiList = beansOfType.toMap().map {

            dubboRefBeanManager.addRefBean(it.value.ref.javaClass.name, it.value.ref)

            val dubboAPI = DubboExposedAPI()
            dubboAPI.name = it.value.beanName
            dubboAPI.refName = it.value.ref.javaClass.name

            val sd = ServiceDescriptor(it.value.interfaceClass)

            dubboAPI.exposedMethods = sd.allMethods.map { mh ->
                val exposedMethod = DubboExposedAPI.ExposedMethod(
                        mh.paramDesc,
                        mh.compatibleParamSignatures,
                        mh.parameterClasses.map { it.name }.toTypedArray(),
                        mh.parameterNames,
                        mh.returnClass.name,
                        mh.returnTypes.map { it.typeName }.toTypedArray(),
                        mh.methodName,
                        mh.isGeneric
                )
                exposedMethod
            }
            dubboAPI
        }

        val request = RemotingCommand.createRequestCommand(RequestCode.REPORT_EXPOSED_API, null)
        body.service = ConfigManager.getInstance().application.map { it.name }.orElse(agentConfigurationProperties.service)
        request.body = body.encode()

        return RemotingTask(
                taskId = reportExposedInterfacesTask,
                type = RemotingTaskType.SYNC,
                request = request,
                timeoutMills = 1000000L,
                invokeCallback = InvokeCallback { })
    }

    /**
     * registryRemotingTask
     */
    override fun registryRemotingTask() {

        // 1 Generate report client info interfaces
        agentImpl.addTask(reportClientInfoTask())

        // 2 Generate report dubbo application exposed interfaces
        agentImpl.addTask(reportExposedInterfacesTask())
    }

    /**
     * Request process handelr
     */
    override fun registryRemotingHandler() {
        // 1 Add invoke api handler
        agentImpl.addHandler(RequestCode.INVOKE_API, dubboInvokerAPIHandler)
    }

    override fun getAgent(): Agent = this.agentImpl

}