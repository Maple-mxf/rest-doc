package restdoc.web.core.schedule.processor

import io.netty.channel.ChannelHandlerContext
import org.springframework.stereotype.Component
import restdoc.remoting.common.ApplicationType
import restdoc.remoting.common.body.DubboExposedAPIBody
import restdoc.remoting.common.body.RestWebExposedAPIBody
import restdoc.remoting.common.body.SpringCloudExposeAPIBody
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode
import restdoc.web.core.schedule.ClientExposedAPIManager
import java.net.InetSocketAddress

/**
 * The ApplicationAPIRequestProcessor provided collect client api service
 */
@Component
class CollectClientAPIRequestProcessor(private val clientExposedAPIManager: ClientExposedAPIManager) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean = false

    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {

        val remoteAddress: InetSocketAddress = ctx.channel().remoteAddress() as InetSocketAddress
        val serviceAddress = "tcp://${remoteAddress.address.hostAddress}:${remoteAddress.port}"

        val on = RemotingSerializable.decode(request.body, com.fasterxml.jackson.databind.node.ObjectNode::class.java)

        when (ApplicationType.valueOf(on.get("applicationType").asText())) {
            ApplicationType.SPRINGCLOUD -> {
                val body = RemotingSerializable.decode(request.body, SpringCloudExposeAPIBody::class.java)
                clientExposedAPIManager.registerAPI(ApplicationType.SPRINGCLOUD, serviceAddress, body.service, body.apiList)
            }
            ApplicationType.DUBBO -> {
                val body = RemotingSerializable.decode(request.body, DubboExposedAPIBody::class.java)
                clientExposedAPIManager.registerAPI(ApplicationType.DUBBO, serviceAddress, body.service, body.apiList)
            }
            ApplicationType.REST_WEB -> {
                val body = RemotingSerializable.decode(request.body, RestWebExposedAPIBody::class.java)
                clientExposedAPIManager.registerAPI(ApplicationType.REST_WEB, serviceAddress, body.service, body.apiList)
            }
            else -> {
                println("Error")
            }
        }
        return RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)
    }
}