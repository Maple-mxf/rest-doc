package restdoc.web.core.schedule

import io.netty.channel.ChannelHandlerContext
import org.springframework.stereotype.Component
import restdoc.remoting.ClientChannelInfo
import restdoc.remoting.common.body.ReportClientInfoRequestBody
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.LanguageCode
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode
import java.net.InetSocketAddress

@Component
class ReportClientInfoRequestProcessor(private val clientManager: ClientManager) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean {
        return false
    }

    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val body = RemotingSerializable.decode(request.body, ReportClientInfoRequestBody::class.java)

        val address = ctx.channel().remoteAddress() as InetSocketAddress

        val clientChannelInfo = ClientChannelInfo(ctx.channel(), address.address.hostAddress, LanguageCode.JAVA, 1)
        clientChannelInfo.hostname = body.hostname
        clientChannelInfo.osname = body.osname

        clientManager.registerClient(clientChannelInfo.clientId, clientChannelInfo)

        return RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)
    }
}