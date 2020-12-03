package restdoc.client.restweb.handler

import io.netty.channel.ChannelHandlerContext
import org.springframework.core.env.Environment
import restdoc.client.api.AgentConfigurationProperties
import restdoc.client.api.model.ClientInfo
import restdoc.client.api.model.RestWebInvocation
import restdoc.client.restweb.RestWebInvokerImpl
import restdoc.client.restweb.context.EndpointsListener
import restdoc.remoting.common.ApplicationType
import restdoc.remoting.common.RemotingUtil
import restdoc.remoting.common.body.RestWebExposedAPIBody
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode

/**
 * InvokerAPIHandler
 */
open class InvokerAPIHandler(private val restWebInvokerImpl: RestWebInvokerImpl) : NettyRequestProcessor {

    override fun rejectRequest() = false

    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val invocation = RemotingSerializable.decode(request.body, RestWebInvocation::class.java)
        val invocationResult = restWebInvokerImpl.rpcInvoke(invocation)
        val response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)
        response.body = invocationResult.encode()
        return response
    }
}

/**
 * ReportClientInfoHandler
 */
open class ReportClientInfoHandler(private val agentConfigurationProperties: AgentConfigurationProperties,
                                   private val environment: Environment
) : NettyRequestProcessor {
    override fun rejectRequest(): Boolean = false
    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)
        val serializationProtocol = "http"

        var service = environment.getProperty("server.servlet.context-path", "")

        if (service == null || service.isBlank()) service = agentConfigurationProperties.service
        if (service == null || service.isBlank()) service = "未命名的服务"

        /*if (service.isBlank()) service = buildProperties.artifact*/

        val body = ClientInfo(
                osname = System.getProperty("os.name", "Windows 10"),
                hostname = RemotingUtil.getHostname(),
                service = service,
                type = ApplicationType.REST_WEB,
                serializationProtocol = serializationProtocol)

        response.body = body.encode()
        return response
    }
}

/**
 * ExportAPIHandler
 */
open class ExportAPIHandler(
        private val agentConfigurationProperties: AgentConfigurationProperties,
        private val endpointsListener: EndpointsListener,
        private val environment: Environment) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean = false
    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand?): RemotingCommand {
        val body = RestWebExposedAPIBody()
        body.apiList = endpointsListener.restWebExposedAPIList
        var service = environment.getProperty("server.servlet.context-path", "")

        if (service == null || service.isBlank()) service = agentConfigurationProperties.service
        if (service == null || service.isBlank()) service = "未命名的服务"
        body.service = service

        val response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)
        response.body = body.encode()
        return response
    }
}