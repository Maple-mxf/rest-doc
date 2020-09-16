package restdoc.web.core.schedule

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import restdoc.client.api.model.RestWebInvocation
import restdoc.client.api.model.RestWebInvocationResult
import restdoc.remoting.common.RequestCode
import restdoc.remoting.common.RestWebExposedAPI
import restdoc.remoting.common.body.RestWebExposedAPIBody
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
import restdoc.web.core.schedule.processor.CollectClientAPIRequestProcessor
import restdoc.web.core.schedule.processor.CollectClientInfoRequestProcessor
import java.util.concurrent.CopyOnWriteArrayList

/**
 * ScheduleServer provided the tcp server dashboard
 *
 * @author Overman
 */
@Component
class ScheduleController @Autowired constructor(scheduleProperties: ScheduleProperties,
                                                private val clientManager: ClientChannelManager,
                                                collectClientInfoRequestProcessor: CollectClientInfoRequestProcessor,
                                                collectClientAPIRequestProcessor: CollectClientAPIRequestProcessor
) : CommandLineRunner {

    private val log: Logger = LoggerFactory.getLogger(ScheduleController::class.java)

    private val remotingTasks: CopyOnWriteArrayList<RemotingTask> = CopyOnWriteArrayList()

    private val httpTaskExecuteTimeout = (32 shl 9).toLong()

    private val thread: Thread = Thread(Runnable {
        this.remotingServer.start()
    })

    private val remotingServer: NettyRemotingServer;

    init {
        val config = NettyServerConfig()
        config.listenPort = scheduleProperties.port
        remotingServer = NettyRemotingServer(config)

        this.remotingServer.registerProcessor(RequestCode.REPORT_CLIENT_INFO, collectClientInfoRequestProcessor, null)
        this.remotingServer.registerProcessor(RequestCode.REPORT_EXPOSED_API, collectClientAPIRequestProcessor, null)
    }

    override fun run(vararg args: String?) {

        this.thread.start()

        log.info("ScheduleController started")
    }


    fun executeRemotingTask(clientId: String,  remotingTask: RemotingTask): RemotingTaskExecuteResult {
        val clientChannelInfo = clientManager.findClientByRemoteAddress(clientId)
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

    @Deprecated(message = "")
    @Throws(InterruptedException::class,
            RemotingTimeoutException::class,
            RemotingSendRequestException::class,
            RemotingCommandException::class)
    fun syncSubmitRemoteHttpTask(clientId: String?,
                                 taskId: String?,
                                 invocation: RestWebInvocation): RestWebInvocationResult {

        val header = SubmitHttpTaskRequestHeader()
        header.taskId = taskId
        val request = RemotingCommand.createRequestCommand(RequestCode.SUBMIT_HTTP_PROCESS, header)
        request.body = invocation.encode()
        val clientChannelInfo = clientManager.findClient(clientId)

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

    @Deprecated(message = "")
    fun syncGetEmptyApiTemplates(clientId: String?): List<RestWebExposedAPI> {

        val request = RemotingCommand.createRequestCommand(RequestCode.GET_EMPTY_API_TEMPLATES, null)
        val clientChannelInfo = clientManager.findClient(clientId)

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