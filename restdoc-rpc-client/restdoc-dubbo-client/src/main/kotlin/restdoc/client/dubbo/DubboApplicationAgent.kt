package restdoc.client.dubbo

import io.netty.channel.Channel
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import restdoc.client.api.Agent
import restdoc.client.api.Status
import restdoc.remoting.netty.ResponseFuture
import restdoc.remoting.protocol.RemotingCommand

/**
 * Agent proxy server request to inner service
 */
class DubboApplicationAgent : Agent {

    private val beanFactory: ConfigurableListableBeanFactory

    constructor(beanFactory: ConfigurableListableBeanFactory) {
        this.beanFactory = beanFactory
    }

    override fun start() {
        TODO("")
    }

    override fun handler(): RemotingCommand {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getClientStatus(): Status {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun connect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disconnect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun syncInvoke(cmd: RemotingCommand, channel: Channel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun asyncInvoke(cmd: RemotingCommand, channel: Channel, future: ResponseFuture) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}