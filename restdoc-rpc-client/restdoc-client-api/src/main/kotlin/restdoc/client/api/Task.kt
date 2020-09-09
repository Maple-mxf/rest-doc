package restdoc.client.api

import restdoc.remoting.InvokeCallback
import restdoc.remoting.protocol.RemotingCommand

enum class RemotingTaskType {
    /**
     */
    ONE_WAY,
    SYNC,
    ASYNC
}

data class RemotingTask(
        val taskId: String,
        val type: RemotingTaskType,
        val request: RemotingCommand,
        val timeoutMills: Long,
        val invokeCallback: InvokeCallback)
