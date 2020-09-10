package restdoc.web.core.schedule.processor

import io.netty.channel.ChannelHandlerContext
import org.springframework.stereotype.Component
import restdoc.remoting.common.ApplicationClientInfo
import restdoc.remoting.common.body.ClientInfoBody
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.LanguageCode
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode
import restdoc.web.core.schedule.ClientChannelManager
import java.net.InetSocketAddress


/**
 * ApplicationClientRequestProcessor
 */
@Component
class ApplicationClientRequestProcessor(private val clientManager: ClientChannelManager) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean {
        return false
    }

    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val body = RemotingSerializable.decode(request.body, ClientInfoBody::class.java)

        val address = ctx.channel().remoteAddress() as InetSocketAddress

        val clientChannelInfo = ApplicationClientInfo(
                ctx.channel(),
                "tcp://${address.address.hostAddress}:${address.port}",
                LanguageCode.JAVA,
                1)

        clientChannelInfo.hostname = body.hostname
        clientChannelInfo.osname = body.osname
        clientChannelInfo.service = body.service
        clientChannelInfo.serializationProtocol = body.serializationProtocol
        clientChannelInfo.applicationType = body.applicationType

        clientManager.registerClient(clientChannelInfo.id, clientChannelInfo)

        return RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)
    }
}