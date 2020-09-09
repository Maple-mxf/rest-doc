package restdoc.client.dubbo

import org.apache.dubbo.config.spring.ServiceBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import restdoc.client.api.*
import restdoc.client.dubbo.model.ServiceDescriptor
import restdoc.remoting.InvokeCallback
import restdoc.remoting.common.DubboExposedAPI
import restdoc.remoting.common.RequestCode
import restdoc.remoting.common.body.DubboExposedAPIBody
import restdoc.remoting.protocol.RemotingCommand

@Configuration
@Import(AgentConfigurationProperties::class, AgentConfiguration::class)
@ConditionalOnClass(value = [AgentConfiguration::class])
open class DubboAgentClientConfiguration : CommandLineRunner {

    @Autowired
    lateinit var beanFactory: ConfigurableListableBeanFactory

    @Autowired
    lateinit var agentImpl: AgentImpl

    override fun run(vararg args: String?) {
        // 1 Registry remote task
        registryTask()

        // 2 Start client agent
        agentImpl.start()

        // 3 Invoke report client exposed task
        agentImpl.invoke(reportExposedInterfacesTask)
    }

    private fun registryTask() {

        // 1 Generate report dubbo application exposed interfaces
        agentImpl.addTask(genReportExposedInterfacesTask())

        // 2 Generate report client info interfaces

    }

    private fun genReportExposedInterfacesTask(): RemotingTask {
        val body = DubboExposedAPIBody()

        val beansOfType = beanFactory.getBeansOfType(ServiceBean::class.java)
        body.apiList = beansOfType.toMap().map {
            val dubboAPI = DubboExposedAPI()
            dubboAPI.name = it.value.beanName

            val sd = ServiceDescriptor(it.value.interfaceClass)

            dubboAPI.exposedMethods = sd.allMethods.map { mh ->
                DubboExposedAPI.ExposedMethod(
                        mh.method,
                        mh.paramDesc,
                        mh.compatibleParamSignatures,
                        mh.parameterClasses,
                        mh.returnClass,
                        mh.returnTypes,
                        mh.methodName,
                        mh.isGeneric
                )
            }
            dubboAPI
        }

        val request = RemotingCommand.createRequestCommand(RequestCode.REPORT_EXPOSED_API, null)
        request.body = body.encode()

        return RemotingTask(
                taskId = reportExposedInterfacesTask,
                type = RemotingTaskType.SYNC,
                request = request,
                timeoutMills = 10000L,
                invokeCallback = InvokeCallback { })
    }
}