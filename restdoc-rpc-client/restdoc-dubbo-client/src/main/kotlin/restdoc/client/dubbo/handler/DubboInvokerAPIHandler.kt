package restdoc.client.dubbo.handler

import io.netty.channel.ChannelHandlerContext
import org.springframework.stereotype.Component
import restdoc.client.dubbo.DubboInvokerImpl
import restdoc.client.api.model.DubboInvocation
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode

/**
 * InvokerDubboAPIHandler
 */
@Component
class DubboInvokerAPIHandler(val dubboInvokerImpl: DubboInvokerImpl) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean = false

    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val invocation = RemotingSerializable.decode(request.body, DubboInvocation::class.java)
        val invocationResult = dubboInvokerImpl.invoke(invocation)

        val response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)
        response.body = invocationResult.encode()

        return response
    }
}