package restdoc.client.api

import io.netty.channel.Channel
import restdoc.remoting.netty.ResponseFuture
import restdoc.remoting.protocol.RemotingCommand


/**
 * The Agent provided client define function
 */
interface Agent {

    /**
     * Start client channel
     */
    fun start()

    /**
     * Handler the server given request
     */
    fun handler(): RemotingCommand

    /**
     * Get client server status
     */
    fun getClientStatus(): Status

    /**
     * Connect to server
     */
    fun connect()

    /**
     * Disconnect from server
     */
    fun disconnect()

    /**
     * Sync invoke server
     */
    fun syncInvoke(cmd: RemotingCommand, channel: Channel)

    /**
     * Async invoke server
     */
    fun asyncInvoke(cmd: RemotingCommand, channel: Channel, future: ResponseFuture)
}