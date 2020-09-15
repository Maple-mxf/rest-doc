package restdoc.client.api

import io.netty.channel.Channel
import restdoc.remoting.ChannelEventListener
import restdoc.remoting.RemotingClient
import restdoc.remoting.netty.NettyClientConfig
import restdoc.remoting.netty.NettyRemotingClient
import restdoc.remoting.netty.NettyRequestProcessor
import java.util.concurrent.CopyOnWriteArrayList

/**
 * The Agent interface implement
 */
class AgentImpl(private val agentConfigurationProperties: AgentConfigurationProperties) : Agent {

    private val remotingClient: NettyRemotingClient

    private var status: Status = Status.STARTED

    private val remotingTasks: CopyOnWriteArrayList<RemotingTask> = CopyOnWriteArrayList()

    init {
        val config = NettyClientConfig()
        config.host = agentConfigurationProperties.host
        config.port = agentConfigurationProperties.port


        val channelEventListener = object : ChannelEventListener {
            override fun onChannelConnect(remoteAddr: String?, channel: Channel) {}
            override fun onChannelException(remoteAddr: String?, channel: Channel) {}
            override fun onChannelIdle(remoteAddr: String?, channel: Channel) {}
            override fun onChannelClose(remoteAddr: String?, channel: Channel) {
                reconnect()
            }
        }

        this.remotingClient = NettyRemotingClient(config, channelEventListener)
    }

    override fun addTask(task: RemotingTask) {
        this.remotingTasks.add(task)
    }

    override fun getRemotingClient(): RemotingClient {
        return this.remotingClient
    }

    override fun start() {
        this.remotingClient.start()
    }

    override fun getClientStatus(): Status {
        return this.status
    }

    override fun disconnect() {
        remotingClient.shutdown()
    }

    override fun reconnect() {

    }

    override fun getServerRemoteAddress(): String {
        return this.agentConfigurationProperties.host + ":" + this.agentConfigurationProperties.port
    }

    override fun addHandler(code: Int, handler: NettyRequestProcessor) {
        remotingClient.registerProcessor(code, handler, null)
    }

    @Throws(exceptionClasses = [NoSuchElementException::class])
    override fun invoke(taskId: String): InvokeResult {
        val remotingTask = remotingTasks.first { it.taskId == taskId }

        return when (remotingTask.type) {
            RemotingTaskType.ASYNC -> {
                this.getRemotingClient().invokeAsync(
                        this.getServerRemoteAddress(),
                        remotingTask.request, remotingTask.timeoutMills, remotingTask.invokeCallback)
                empty()
            }
            RemotingTaskType.SYNC -> {
                val response = this.getRemotingClient()
                        .invokeSync(this.getServerRemoteAddress(), remotingTask.request, remotingTask.timeoutMills)
                InvokeResult(response = response)
            }
            RemotingTaskType.ONE_WAY -> {
                this.getRemotingClient().invokeOneway(
                        this.getServerRemoteAddress(),
                        remotingTask.request,
                        remotingTask.timeoutMills
                )
                empty()
            }
        }
    }
}