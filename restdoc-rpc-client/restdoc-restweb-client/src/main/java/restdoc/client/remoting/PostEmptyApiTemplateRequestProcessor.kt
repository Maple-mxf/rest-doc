package restdoc.client.remoting

import io.netty.channel.ChannelHandlerContext
import restdoc.client.context.EndpointsListener
import restdoc.remoting.common.body.RestWebExposedAPIBody
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSysResponseCode

class PostEmptyApiTemplateRequestProcessor(private val endpointsListener: EndpointsListener) : NettyRequestProcessor {

    @Throws(Exception::class)
    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS,
                "OK")
        val body = RestWebExposedAPIBody()
        body.apiList = endpointsListener.emptyApiTemplates
        response.body = body.encode()
        return response
    }

    override fun rejectRequest(): Boolean {
        return false
    }

}