package restdoc.client.dubbo.handler

import io.netty.channel.ChannelHandlerContext
import org.apache.dubbo.config.ProtocolConfig
import org.apache.dubbo.config.context.ConfigManager
import org.apache.dubbo.config.spring.ServiceBean
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
import restdoc.client.api.AgentConfigurationProperties
import restdoc.client.api.model.ClientInfo
import restdoc.client.dubbo.DubboInvokerImpl
import restdoc.client.api.model.DubboInvocation
import restdoc.client.dubbo.DubboRefBeanManager
import restdoc.client.dubbo.model.ServiceDescriptor
import restdoc.remoting.common.ApplicationType
import restdoc.remoting.common.DubboApiDescriptor
import restdoc.remoting.common.RemotingUtil
import restdoc.remoting.common.body.DubboExposedAPIBody
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode

/**
 * InvokerDubboAPIHandler
 */
@Component
open class InvokeAPIHandler(val dubboInvokerImpl: DubboInvokerImpl) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean = false

    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val invocation = RemotingSerializable.decode(request.body, DubboInvocation::class.java)
        val invocationResult = dubboInvokerImpl.rpcInvoke(invocation)

        val response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)
        response.body = invocationResult.encode()

        return response
    }
}


/**
 * ReportClientInfoHandler
 */
@Component
open class ReportClientInfoHandler(private val beanFactory: ConfigurableListableBeanFactory,
                                   private val agentConfigurationProperties: AgentConfigurationProperties) : NettyRequestProcessor {
    override fun rejectRequest(): Boolean = false
    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)

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

        response.body = body.encode()
        return response
    }
}


@Component
open class ExportAPIHandler(private val beanFactory: ConfigurableListableBeanFactory,
                            private val dubboRefBeanManager: DubboRefBeanManager,
                            private val agentConfigurationProperties: AgentConfigurationProperties) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean = false
    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)

        val body = DubboExposedAPIBody()

        val beansOfType = beanFactory.getBeansOfType(ServiceBean::class.java)
        body.apiList = beansOfType.toMap().map {

            dubboRefBeanManager.addRefBean(it.value.ref.javaClass.name, it.value.ref)

            val dubboAPI = DubboApiDescriptor()
            dubboAPI.name = it.value.beanName
            dubboAPI.refName = it.value.ref.javaClass.name

            val sd = ServiceDescriptor(it.value.interfaceClass)

            dubboAPI.exposedMethods = sd.allMethods.map { mh ->
                val exposedMethod = DubboApiDescriptor.ExposedMethod(
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
        body.service = ConfigManager.getInstance().application.map { it.name }.orElse(agentConfigurationProperties.service)
        response.body = body.encode()

        return response
    }
}