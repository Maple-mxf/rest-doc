package smartdoc.dashboard.schedule

import io.netty.channel.Channel
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import restdoc.client.api.model.ClientInfo
import restdoc.remoting.ChannelEventListener
import restdoc.remoting.common.RequestCode
import restdoc.remoting.netty.NettyRemotingServer
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.netty.NettyServerConfig
import restdoc.remoting.protocol.LanguageCode
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.rpc.client.common.model.ApplicationClientInfo
import smartdoc.dashboard.schedule.handler.AcknowledgeVersionHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

interface ScheduleService : CommandLineRunner {

    fun scheduleProperties(): ScheduleProperties

    fun start()

    fun addHandler(code: Int, handler: NettyRequestProcessor)

    fun addTask(code: Int, task: RemotingTaskWrapper)

    fun <R> schedule(clientId: String, code: Int): R

    fun scheduleAsync(clientId: String, code: Int)
}

@Component("scheduleServiceAdapterImpl")
class ScheduleServiceAdapterImpl(private val scheduleProperties: ScheduleProperties,
                                 val clientManager: ClientManager,
                                 val apiManager: ApiManager) : ScheduleService {

    private val remotingServer: NettyRemotingServer
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val tasks: MutableMap<Int, RemotingTaskWrapper> = ConcurrentHashMap()

    override fun <R> schedule(clientId: String, code: Int): R =
            clientManager.schedule(clientId, tasks[code]!!) as R

    override fun scheduleAsync(clientId: String, code: Int) {
        clientManager.schedule(clientId, tasks[code]!!)
    }

    override fun scheduleProperties(): ScheduleProperties = scheduleProperties

    override fun addHandler(code: Int, handler: NettyRequestProcessor) {
        this.remotingServer.registerProcessor(code, handler, null)
    }

    @Deprecated(message = "addTask")
    override fun addTask(code: Int, task: RemotingTaskWrapper) {
        this.tasks[code] = task
    }

    override fun start() {
        executor.submit {
            remotingServer.start()
        }
    }

    override fun run(vararg args: String?) {
        start()
    }

    init {
        // 1 Init remoting server
        val config = NettyServerConfig()
        config.listenPort = scheduleProperties.port
        remotingServer = NettyRemotingServer(config, ScheduleServiceChannelEventListener())

        // 2 Init request task
        val collectClientInfoTask = RemotingTaskWrapper()
        collectClientInfoTask.apply {
            this.async = false
            this.command = RemotingCommand.createRequestCommand(RequestCode.CollectClientInfo, null)
            responseType = ClientInfo::class.java
        }

        val collectClientApiTask = RemotingTaskWrapper()
        collectClientApiTask.apply {
            this.async = false
            this.command = RemotingCommand.createRequestCommand(RequestCode.CollectApi, null)
            responseType = ClientInfo::class.java
        }

        this.addTask(RequestCode.CollectClientInfo, collectClientInfoTask)
        this.addTask(RequestCode.CollectApi, collectClientApiTask)

        // 3 Init handler
        addHandler(RequestCode.AcknowledgeVersion, AcknowledgeVersionHandler())
    }

    private inner class ScheduleServiceChannelEventListener : ChannelEventListener {
        override fun onChannelConnect(remote: String, channel: Channel) {

            if (channel.isActive && channel.isOpen && channel.isRegistered && channel.isWritable){
                // Instance Client
                clientManager.add(instanceClient(channel), remotingServer)
                System.err.println("start client")
            }
        }

        override fun onChannelClose(remote: String, channel: Channel) {
            clientManager.get(channel)?.stop()
        }

        override fun onChannelException(remote: String, channel: Channel, cause: Throwable) {
            clientManager.get(channel)?.onException(cause)
        }

        override fun onChannelIdle(remote: String, channel: Channel) {
            clientManager.get(channel)?.idle()
        }

        private fun instanceClient(channel: Channel): ApplicationClientInfo {
            val request = RemotingCommand.createRequestCommand(RequestCode.CollectClientInfo, null)
            val response = remotingServer.invokeSync(channel, request, 10000L)
            val body = RemotingSerializable.decode(response.body, ClientInfo::class.java)

            return ApplicationClientInfo(channel,
                    LanguageCode.JAVA, 1)
                    .apply {
                        hostname = body.hostname
                        osname = body.osname
                        service = body.service
                        serializationProtocol = body.serializationProtocol
                        applicationType = body.type
                    }
        }
    }
}