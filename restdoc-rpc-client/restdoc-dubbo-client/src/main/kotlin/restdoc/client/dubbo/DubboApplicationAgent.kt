package restdoc.client.dubbo

import io.netty.channel.Channel
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import restdoc.client.api.Agent
import restdoc.client.api.Status
import restdoc.remoting.RemotingClient
import restdoc.remoting.netty.NettyClientConfig
import restdoc.remoting.netty.NettyRemotingClient
import restdoc.remoting.netty.ResponseFuture
import restdoc.remoting.protocol.RemotingCommand

/**
 * Agent proxy server request to inner service
 */
class DubboApplicationAgent : Agent {

    private val beanFactory: ConfigurableListableBeanFactory

    private val remotingClient: RemotingClient

    constructor(beanFactory: ConfigurableListableBeanFactory) {
        this.beanFactory = beanFactory
        val config = NettyClientConfig()
        this.remotingClient = NettyRemotingClient(config)
    }

    override fun start() {
        remotingClient.start()
    }

    override fun handler(): RemotingCommand {
    }

    override fun getClientStatus(): Status {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun connect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disconnect() {
        remotingClient.shutdown()
    }

    override fun syncInvoke(cmd: RemotingCommand, channel: Channel) {
    }

    override fun asyncInvoke(cmd: RemotingCommand, channel: Channel, future: ResponseFuture) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}