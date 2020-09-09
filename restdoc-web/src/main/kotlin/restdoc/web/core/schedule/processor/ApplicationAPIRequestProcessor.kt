package restdoc.web.core.schedule.processor

import io.netty.channel.ChannelHandlerContext
import org.springframework.stereotype.Component
import restdoc.remoting.common.ApplicationType
import restdoc.remoting.common.body.DubboExposedAPIBody
import restdoc.remoting.common.body.RestWebExposedAPIBody
import restdoc.remoting.common.body.SpringCloudExposeAPIBody
import restdoc.remoting.common.header.ExposedAPIHeader
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode
import restdoc.web.core.schedule.ExposedAPIManager
import java.net.InetSocketAddress

/**
 * The ApplicationAPIRequestProcessor provided collect client api service
 */
@Component
class ApplicationAPIRequestProcessor(private val exposedAPIManager: ExposedAPIManager) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean = false

    override fun processRequest(ctx: ChannelHandlerContext, request: RemotingCommand): RemotingCommand {
        val header = request.readCustomHeader() as ExposedAPIHeader

        val remoteAddress: InetSocketAddress = ctx.channel().remoteAddress() as InetSocketAddress
        val serviceAddress = "tcp://${remoteAddress.address.hostAddress}:${remoteAddress.port}"


        when (header.applicationType) {
            ApplicationType.SPRINGCLOUD -> {
                val body = RemotingSerializable.decode(request.body, SpringCloudExposeAPIBody::class.java)
                exposedAPIManager.registerAPI(ApplicationType.SPRINGCLOUD, serviceAddress, body.service, body.apiList)
            }
            ApplicationType.DUBBO -> {
                val body = RemotingSerializable.decode(request.body, DubboExposedAPIBody::class.java)
                exposedAPIManager.registerAPI(ApplicationType.DUBBO, serviceAddress, body.service, body.apiList)
            }
            ApplicationType.REST_WEB -> {
                val body = RemotingSerializable.decode(request.body, RestWebExposedAPIBody::class.java)
                exposedAPIManager.registerAPI(ApplicationType.REST_WEB, serviceAddress, body.service, body.apiList)
            }
            else -> {
                println("Error")
            }
        }
        return RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null)
    }
}