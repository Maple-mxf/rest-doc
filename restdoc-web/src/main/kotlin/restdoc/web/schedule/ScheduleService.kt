package restdoc.web.schedule

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
import java.net.InetSocketAddress
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
open class ScheduleServiceAdapterImpl(private val scheduleProperties: ScheduleProperties,
                                      private val clientManager: ClientManager) : ScheduleService {

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
        val config = NettyServerConfig()
        config.listenPort = scheduleProperties.port
        remotingServer = NettyRemotingServer(config, ScheduleServiceChannelEventListener())

        val collectClientInfoTask = RemotingTaskWrapper()
        collectClientInfoTask.apply {
            this.async = false
            this.command = RemotingCommand.createRequestCommand(RequestCode.GET_CLIENT_INFO, null)
            responseType = ClientInfo::class.java
        }

        val collectClientApiTask = RemotingTaskWrapper()
        collectClientApiTask.apply {
            this.async = false
            this.command = RemotingCommand.createRequestCommand(RequestCode.GET_EXPOSED_API, null)
            responseType = ClientInfo::class.java
        }

        this.addTask(RequestCode.GET_CLIENT_INFO, collectClientInfoTask)
        this.addTask(RequestCode.GET_EXPOSED_API, collectClientApiTask)
    }

    private inner class ScheduleServiceChannelEventListener : ChannelEventListener {
        override fun onChannelConnect(remote: String, channel: Channel) {

            // Init Client point
            val request = RemotingCommand.createRequestCommand(RequestCode.GET_CLIENT_INFO, null)
            val response = remotingServer.invokeSync(channel, request, 10000L)
            val body = RemotingSerializable.decode(response.body, ClientInfo::class.java)
            val address = channel.remoteAddress() as InetSocketAddress
            val aci = ApplicationClientInfo(null, channel, "tcp://${address.address.hostAddress}:${address.port}", LanguageCode.JAVA, 1)
                    .apply {
                        hostname = body.hostname
                        osname = body.osname
                        service = body.service
                        serializationProtocol = body.serializationProtocol
                        applicationType = body.type
                    }

            val adapter = clientManager.add(aci, remotingServer)
        }

        override fun onChannelClose(remote: String, channel: Channel) {
        }

        override fun onChannelException(remote: String, channel: Channel) {
        }

        override fun onChannelIdle(remote: String, channel: Channel) {
        }
    }
}