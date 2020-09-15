package restdoc.client.restweb.handler

import io.netty.channel.ChannelHandlerContext
import restdoc.client.api.model.RestWebInvocation
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable

class RestWebInvokerAPIHandler : NettyRequestProcessor {

    override fun rejectRequest() = false

    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val invocation = RemotingSerializable.decode(request.body, RestWebInvocation::class.java)


    }
}