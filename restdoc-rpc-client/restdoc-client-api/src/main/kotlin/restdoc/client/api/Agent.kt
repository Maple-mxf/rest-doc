package restdoc.client.api

import restdoc.remoting.RemotingClient


/**
 * The Agent provided client define function
 */
interface Agent {

    /**
     * Get remoting client
     */
    fun getRemotingClient(): RemotingClient

    /**
     * Start client channel
     */
    fun start()

    /**
     * Get client server status
     */
    fun getClientStatus(): Status

    /**
     * Disconnect from server
     */
    fun disconnect()

    /**
     * Get ServerRemoteAddress
     */
    fun getServerRemoteAddress(): String


    /**
     * Invoke Task
     */
    @Throws(exceptionClasses = [NoSuchElementException::class])
    fun invoke(taskId: String):InvokeResult

    /**
     *
     */
    fun addTask(task: RemotingTask)
}