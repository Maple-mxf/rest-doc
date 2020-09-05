package restdoc.web.core.schedule

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import restdoc.remoting.common.RequestCode
import restdoc.remoting.common.body.PostHttpTaskExecuteResultRequestBody
import restdoc.remoting.common.body.SubmitHttpTaskRequestBody
import restdoc.remoting.common.header.PostHttpTaskExecuteResultRequestHeader
import restdoc.remoting.common.header.SubmitHttpTaskRequestHeader
import restdoc.remoting.exception.RemotingCommandException
import restdoc.remoting.exception.RemotingSendRequestException
import restdoc.remoting.exception.RemotingTimeoutException
import restdoc.remoting.netty.NettyRemotingServer
import restdoc.remoting.netty.NettyServerConfig
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode
import restdoc.web.core.ServiceException
import restdoc.web.core.Status

/**
 * ScheduleServer provided the tcp server dashboard
 *
 * @author ubuntu-m
 */
@Component
class ScheduleController(val scheduleProperties: ScheduleProperties,
                         val clientManager: ClientManager) : CommandLineRunner {

    private val log: Logger = LoggerFactory.getLogger(ScheduleController::class.java)

    private val httpTaskExecuteTimeout = (32 shl 9).toLong()

    private val thread: Thread = Thread(Runnable {
        this.remotingServer.start()
    })

    private val remotingServer: NettyRemotingServer
        get() {
            val nettyServerConfig = NettyServerConfig()
            nettyServerConfig.listenPort = scheduleProperties.port
            return NettyRemotingServer(nettyServerConfig)
        }

    fun initialize() {
        this.remotingServer.registerProcessor(RequestCode.REPORT_CLIENT_INFO,
                ReportClientInfoRequestProcessor(clientManager), null);
    }

    override fun run(vararg args: String?) {
        this.thread.start()
        log.info("ScheduleController started")
    }

    @Throws(InterruptedException::class, RemotingTimeoutException::class, RemotingSendRequestException::class, RemotingCommandException::class)
    fun syncSubmitRemoteHttpTask(clientId: String?,
                                 taskId: String?,
                                 body: SubmitHttpTaskRequestBody): HttpTaskData? {

        val header = SubmitHttpTaskRequestHeader()
        header.taskId = taskId
        val request = RemotingCommand.createRequestCommand(RequestCode.SUBMIT_HTTP_PROCESS, header)
        request.body = body.encode()
        val clientChannelInfo = clientManager.findClient(clientId)
        val response = remotingServer.invokeSync(clientChannelInfo.channel, request,
                this.httpTaskExecuteTimeout)

        return if (response.code == RemotingSysResponseCode.SUCCESS) {
            val responseBody = RemotingSerializable.decode(response.body,
                    PostHttpTaskExecuteResultRequestBody::class.java)
            val responseHeader = response.decodeCommandCustomHeader(PostHttpTaskExecuteResultRequestHeader::class.java)
                    as PostHttpTaskExecuteResultRequestHeader

            HttpTaskData(
                    responseBody.status,
                    responseBody.responseHeader,
                    responseBody.responseBody,
                    responseHeader.taskId)
        } else {
            throw ServiceException(response.remark, Status.INTERNAL_SERVER_ERROR)
        }
    }
}