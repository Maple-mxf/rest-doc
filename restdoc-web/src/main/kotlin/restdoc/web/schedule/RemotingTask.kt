package restdoc.web.schedule

import restdoc.remoting.InvokeCallback
import restdoc.remoting.protocol.RemotingCommand

/**
 * RemotingTaskType
 */
@Deprecated(message = "RemotingTaskType")
enum class RemotingTaskType {
    /**
     */
    ONE_WAY,
    SYNC,
    ASYNC
}

@Deprecated(message = "RemotingTask")
data class RemotingTask(
        val type: RemotingTaskType,
        val request: RemotingCommand,
        val timeoutMills: Long,
        val invokeCallback: InvokeCallback)


data class RemotingTaskExecuteResult(val success: Boolean = true,
                                     val response: RemotingCommand?
)