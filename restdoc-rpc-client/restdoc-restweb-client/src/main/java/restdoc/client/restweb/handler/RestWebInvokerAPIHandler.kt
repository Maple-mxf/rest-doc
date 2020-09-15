package restdoc.client.restweb.handler

import io.netty.channel.ChannelHandlerContext
import restdoc.client.api.model.RestWebInvocation
import restdoc.client.restweb.RestWebInvokerImpl
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode

class RestWebInvokerAPIHandler(private val restWebInvokerImpl: RestWebInvokerImpl) : NettyRequestProcessor {

    override fun rejectRequest() = false

    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val invocation = RemotingSerializable.decode(request.body, RestWebInvocation::class.java)
        val invocationResult = restWebInvokerImpl.rpcInvoke(invocation)
        val response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)
        response.body = invocationResult.encode()
        return response
    }
}