package restdoc.client.remoting

import io.netty.channel.Channel
import org.slf4j.LoggerFactory
import restdoc.client.config.RestDocProperties
import restdoc.remoting.ChannelEventListener
import restdoc.remoting.common.RemotingUtil
import restdoc.remoting.common.RequestCode
import restdoc.remoting.common.body.ClientInfoBody
import restdoc.remoting.exception.RemotingConnectException
import restdoc.remoting.exception.RemotingSendRequestException
import restdoc.remoting.exception.RemotingTimeoutException
import restdoc.remoting.exception.RemotingTooMuchRequestException
import restdoc.remoting.netty.NettyClientConfig
import restdoc.remoting.netty.NettyRemotingClient
import restdoc.remoting.netty.ResponseFuture
import restdoc.remoting.protocol.RemotingCommand

/**
 * The ApplicationClient class provided start client connect to server
 *
 *
 * Establish channel
 */
class HttpApplicationAgent(restDocProperties: RestDocProperties,
                           httpTaskRequestProcessor: HttpTaskRequestProcessor,
                           postEmptyApiTemplateRequestProcessor: PostEmptyApiTemplateRequestProcessor) {

    private val state = State.STOPPED
    private val httpTaskRequestProcessor: HttpTaskRequestProcessor
    private val postEmptyApiTemplateRequestProcessor: PostEmptyApiTemplateRequestProcessor

    private enum class State {
        STOPPED, RUNNING
    }

    private lateinit var remotingClient: NettyRemotingClient

    private fun registerProcessor() { // Register the given http task request processor
        remotingClient.registerProcessor(RequestCode.SUBMIT_HTTP_PROCESS,
                httpTaskRequestProcessor, null)
        // Register the given api empty template request processor
        remotingClient.registerProcessor(RequestCode.GET_EMPTY_API_TEMPLATES,
                postEmptyApiTemplateRequestProcessor, null)
    }

    @Synchronized
    fun connection() {
        synchronized(this) {
            if (state == State.RUNNING) {
                log.error("ApplicationClient already running")
                return
            }
            Thread(Runnable {
                try {
                    remotingClient.start()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }).start()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(HttpApplicationAgent::class.java)
    }

    init {
        val config = NettyClientConfig()
        config.isUseTLS = false
        config.host = restDocProperties.serverIp
        config.port = restDocProperties.serverPort
        val eventListener: ChannelEventListener = object : ChannelEventListener {
            @Throws(InterruptedException::class, RemotingSendRequestException::class, RemotingTimeoutException::class, RemotingTooMuchRequestException::class, RemotingConnectException::class) override fun onChannelConnect(remoteAddr: String, channel: Channel) {
                val request = RemotingCommand.createRequestCommand(RequestCode.REPORT_CLIENT_INFO, null)
                val body = ClientInfoBody()
                body.osname = System.getProperty("os.name")
                body.hostname = RemotingUtil.getHostname()
                body.service = restDocProperties.service
                request.body = body.encode()
                remotingClient.invokeAsync(
                        remoteAddr,
                        request,
                        3000L
                ) { responseFuture: ResponseFuture ->
                    val response: RemotingCommand = responseFuture.waitResponse(3000)
                    log.info("register client info response code {} ", response)
                }
            }

            // When The channel close
            override fun onChannelClose(remoteAddr: String, channel: Channel) {}

            override fun onChannelException(remoteAddr: String, channel: Channel) {}
            override fun onChannelIdle(remoteAddr: String, channel: Channel) {}
        }
        this.postEmptyApiTemplateRequestProcessor = postEmptyApiTemplateRequestProcessor
        this.httpTaskRequestProcessor = httpTaskRequestProcessor
        remotingClient = NettyRemotingClient(config, eventListener)

        //
        registerProcessor()
    }
}