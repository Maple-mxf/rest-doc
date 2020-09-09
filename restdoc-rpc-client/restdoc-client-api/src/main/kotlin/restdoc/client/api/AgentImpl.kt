package restdoc.client.api

import restdoc.remoting.RemotingClient
import restdoc.remoting.netty.NettyClientConfig
import restdoc.remoting.netty.NettyRemotingClient
import java.util.concurrent.CopyOnWriteArrayList

/**
 * The Agent interface implement
 */
class AgentImpl(agentConfigurationProperties: AgentConfigurationProperties) : Agent {

    private val remotingClient: RemotingClient

    private var status: Status = Status.STARTED

    private val remotingTasks: CopyOnWriteArrayList<RemotingTask> = CopyOnWriteArrayList()

    init {
        val config = NettyClientConfig()
        config.host = agentConfigurationProperties.host
        config.port = agentConfigurationProperties.port
        this.remotingClient = NettyRemotingClient(config)
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

    override fun getServerRemoteAddress(): String {
        return remotingClient.nameServerAddressList[0]
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