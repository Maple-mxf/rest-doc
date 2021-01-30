package smartdoc.dashboard.schedule.handler

import io.netty.channel.ChannelHandlerContext
import restdoc.client.api.model.Version
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSysResponseCode
import smartdoc.dashboard.getCurrentVersion

class AcknowledgeVersionHandler : NettyRequestProcessor {
    override fun rejectRequest() = false

    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null, null)
        val version = Version()
        version.version = getCurrentVersion()
        response.body = version.encode()
        return response
    }
}