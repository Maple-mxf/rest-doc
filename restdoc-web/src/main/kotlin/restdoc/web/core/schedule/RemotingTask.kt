package restdoc.web.core.schedule

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
        val type: RemotingTaskType,
        val request: RemotingCommand,
        val timeoutMills: Long,
        val invokeCallback: InvokeCallback)


data class RemotingTaskExecuteResult(val success: Boolean = true,
                                     val response: RemotingCommand?
)