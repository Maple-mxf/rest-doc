package restdoc.client.api

import restdoc.remoting.netty.NettyRemotingClient
import restdoc.remoting.netty.NettyRequestProcessor


/**
 * The Agent provided client define function
 */
interface Agent {

    /**
     * Get remoting client
     */
    fun getRemotingClient(): NettyRemotingClient

    /**
     * Start client channel
     */
    fun start()

    /**
     * Get client server status
     */
    fun getClientStatus(): Status

    /**
     * Get ServerRemoteAddress
     */
    fun getServerRemoteAddress(): String
    
    /**
     * Invoke Task
     */
    @Throws(exceptionClasses = [NoSuchElementException::class])
    fun invoke(taskId: String): InvokeResult

    /**
     * Invoke task
     */
    fun invoke(remotingTask: RemotingTask): InvokeResult

    /**
     *
     */
    fun addTask(task: RemotingTask)

    /**
     *
     */
    fun addHandler(code: Int, handler: NettyRequestProcessor)
}