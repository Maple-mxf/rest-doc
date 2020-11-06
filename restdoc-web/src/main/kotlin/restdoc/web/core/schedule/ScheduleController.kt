package restdoc.web.core.schedule

import com.fasterxml.jackson.databind.node.ObjectNode
import io.netty.channel.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import restdoc.client.api.model.ClientInfo
import restdoc.client.api.model.RestWebInvocation
import restdoc.client.api.model.RestWebInvocationResult
import restdoc.remoting.ChannelEventListener
import restdoc.remoting.common.*
import restdoc.remoting.common.body.DubboExposedAPIBody
import restdoc.remoting.common.body.RestWebExposedAPIBody
import restdoc.remoting.common.body.SpringCloudExposeAPIBody
import restdoc.remoting.exception.RemotingCommandException
import restdoc.remoting.exception.RemotingSendRequestException
import restdoc.remoting.exception.RemotingTimeoutException
import restdoc.remoting.netty.NettyRemotingServer
import restdoc.remoting.netty.NettyServerConfig
import restdoc.remoting.protocol.LanguageCode
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode
import restdoc.web.core.ServiceException
import restdoc.web.core.Status
import java.net.InetSocketAddress

/**
 * ScheduleServer provided the tcp server dashboard
 */
@Component
class ScheduleController @Autowired constructor(scheduleProperties: ScheduleProperties,
                                                private val clientRegistryCenter: ClientRegistryCenter
) : CommandLineRunner {

    private val log: Logger = LoggerFactory.getLogger(ScheduleController::class.java)

    private val httpTaskExecuteTimeout = (32 shl 9).toLong()

    private val thread: Thread = Thread { this.remotingServer.start() }

    private val remotingServer: NettyRemotingServer

    init {
        val config = NettyServerConfig()
        config.listenPort = scheduleProperties.port
        remotingServer = NettyRemotingServer(config,
                object : ChannelEventListener {
                    override fun onChannelConnect(remoteAddr: String, channel: Channel) {
                        callClient(channel)
                    }

                    override fun onChannelException(remoteAddr: String, channel: Channel) {
                        // unregisterAPI
                    }

                    override fun onChannelIdle(remoteAddr: String, channel: Channel) {
                        // unregisterAPI
                    }

                    override fun onChannelClose(remoteAddr: String, channel: Channel) {
                        val remote = RemotingHelper.parseChannelRemoteAddr(channel)
                        clientRegistryCenter.unregistryClient(remote)
                        clientRegistryCenter.unregistryAPI(remote)
                    }
                })
    }


    private fun callClient(channel: Channel) {
        val getClientInfoRequest =
                RemotingCommand.createRequestCommand(RequestCode.REPORT_CLIENT_INFO, null)

        val getClientAPIListRequest = RemotingCommand.createRequestCommand(RequestCode.REPORT_EXPOSED_API, null)

        this.remotingServer.invokeAsync(channel, getClientInfoRequest, 10000L) {
            if (it.responseCommand.code == RemotingSysResponseCode.SUCCESS) {
                val address = channel.remoteAddress() as InetSocketAddress
                val body = RemotingSerializable.decode(it.responseCommand.body, ClientInfo::class.java)
                val clientChannelInfo = ApplicationClientInfo(
                        channel,
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

                clientRegistryCenter.registryClient(RemotingHelper.parseChannelRemoteAddr(channel), clientChannelInfo)
            }
        }

        this.remotingServer.invokeAsync(channel, getClientAPIListRequest, 10000L) {
            if (it.responseCommand.code == RemotingSysResponseCode.SUCCESS) {
                val on = RemotingSerializable.decode(it.responseCommand.body, ObjectNode::class.java)

                val at = ApplicationType.valueOf(on.get("applicationType").asText())
                val exposedAPIBody = when {
                    ApplicationType.DUBBO == at -> {
                        RemotingSerializable.decode(it.responseCommand.body, DubboExposedAPIBody::class.java) as DubboExposedAPIBody
                    }
                    ApplicationType.REST_WEB == at -> {
                        RemotingSerializable.decode(it.responseCommand.body, RestWebExposedAPIBody::class.java) as RestWebExposedAPIBody
                    }
                    else -> {
                        RemotingSerializable.decode(it.responseCommand.body, SpringCloudExposeAPIBody::class.java) as SpringCloudExposeAPIBody
                    }
                }

                val remote = RemotingHelper.parseChannelRemoteAddr(channel)
                clientRegistryCenter.registryAPI(remote, at, exposedAPIBody.service, exposedAPIBody.apiList)
            }
        }
    }

    override fun run(vararg args: String) {
        this.thread.start()
        log.info("ScheduleController started")
    }


    fun executeRemotingTask(clientId: String, remotingTask: RemotingTask): RemotingTaskExecuteResult {
        val clientChannelInfo = clientRegistryCenter.get(clientId)
        val channel = clientChannelInfo!!.channel

        return when (remotingTask.type) {
            RemotingTaskType.ASYNC -> {
                remotingServer.invokeAsync(channel, remotingTask.request, remotingTask.timeoutMills, remotingTask.invokeCallback)
                RemotingTaskExecuteResult(true, null)
            }
            RemotingTaskType.ONE_WAY -> {
                remotingServer.invokeOneway(channel, remotingTask.request, remotingTask.timeoutMills)
                RemotingTaskExecuteResult(true, null)
            }

            RemotingTaskType.SYNC -> {
                val response = remotingServer.invokeSync(channel, remotingTask.request, remotingTask.timeoutMills)
                RemotingTaskExecuteResult(true, response)
            }
        }
    }


    /**
     * @sample executeRemotingTask
     */
    @Throws(InterruptedException::class,
            RemotingTimeoutException::class,
            RemotingSendRequestException::class,
            RemotingCommandException::class)
    @Deprecated(message = "syncSubmitRemoteHttpTask")
    fun syncSubmitRemoteHttpTask(clientId: String?,
                                 taskId: String?,
                                 invocation: RestWebInvocation): RestWebInvocationResult {

        val request = RemotingCommand.createRequestCommand(RequestCode.INVOKE_API, null)
        request.body = invocation.encode()
        val clientChannelInfo = clientRegistryCenter.get(clientId)

        if (clientChannelInfo == null) Status.BAD_REQUEST.error("找不到对应的客户端$clientId")

        val response = remotingServer.invokeSync(clientChannelInfo!!.channel, request,
                this.httpTaskExecuteTimeout)

        return if (response.code == RemotingSysResponseCode.SUCCESS) {
            val responseBody = RemotingSerializable.decode(response.body,
                    RestWebInvocationResult::class.java)

            responseBody
        } else {
            throw ServiceException(response.remark, Status.INTERNAL_SERVER_ERROR)
        }
    }


    /**
     * @see executeRemotingTask
     * @since 1.0
     */
    @Deprecated(message = "syncGetEmptyApiTemplates")
    fun syncGetEmptyApiTemplates(clientId: String?): List<RestWebExposedAPI> {

        val request = RemotingCommand.createRequestCommand(RequestCode.GET_EMPTY_API_TEMPLATES, null)
        val clientChannelInfo = clientRegistryCenter.get(clientId)

        val response = remotingServer.invokeSync(clientChannelInfo!!.channel, request,
                this.httpTaskExecuteTimeout)

        return if (response.code == RemotingSysResponseCode.SUCCESS) {
            (RemotingSerializable.decode(response.body,
                    RestWebExposedAPIBody::class.java) as RestWebExposedAPIBody).apiList
        } else {
            throw ServiceException(response.remark, Status.INTERNAL_SERVER_ERROR)
        }
    }

}