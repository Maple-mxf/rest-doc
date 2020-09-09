package restdoc.client.dubbo

import restdoc.client.api.BaseAgent
import restdoc.client.api.RemotingTask
import restdoc.client.api.Status
import restdoc.remoting.RemotingClient
import restdoc.remoting.netty.NettyClientConfig
import restdoc.remoting.netty.NettyRemotingClient
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Agent proxy server request to inner service
 */
class DubboApplicationAgent(remotingTasks: CopyOnWriteArrayList<RemotingTask>) : BaseAgent(remotingTasks) {

    private val remotingClient: RemotingClient

    private var status: Status = Status.STARTED

    init {
        val config = NettyClientConfig()
        this.remotingClient = NettyRemotingClient(config)
    }

    override fun getRemotingClient(): RemotingClient {
        return remotingClient
    }

    override fun start() {
        this.remotingClient.start()
        this.status = Status.STARTED
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
}