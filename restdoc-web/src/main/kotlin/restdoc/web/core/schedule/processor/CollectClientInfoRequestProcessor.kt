package restdoc.web.core.schedule.processor

import io.netty.channel.ChannelHandlerContext
import org.springframework.stereotype.Component
import restdoc.client.api.model.ClientInfo
import restdoc.remoting.common.ApplicationClientInfo
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.LanguageCode
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode
import restdoc.web.core.schedule.ClientChannelManager
import java.net.InetSocketAddress


/**
 * ApplicationClientRequestProcessor
 *
 * @author Overman
 */
@Deprecated(message = "CollectClientInfoRequestProcessor")
@Component
class CollectClientInfoRequestProcessor(private val clientManager: ClientChannelManager) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean {
        return false
    }

    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val body = RemotingSerializable.decode(request.body, ClientInfo::class.java)

        val address = ctx.channel().remoteAddress() as InetSocketAddress

        val clientChannelInfo = ApplicationClientInfo(
                ctx.channel(),
                "tcp://${address.address.hostAddress}:${address.port}",
                LanguageCode.JAVA,
                1)
                .apply {
                    hostname = body.hostname
                    osname = body.osname
                    service = body.service
                    serializationProtocol = body.serializationProtocol
                    applicationType = body.type
                }

        clientManager.registerClient(clientChannelInfo.id, clientChannelInfo)

        return RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)
    }
}