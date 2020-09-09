package restdoc.client.api

import java.util.concurrent.CopyOnWriteArrayList

/**
 * BaseAgent
 */
abstract class BaseAgent(val remotingTasks: CopyOnWriteArrayList<RemotingTask>) : Agent {

    override fun addTask(task: RemotingTask) {
        this.remotingTasks.add(task)
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
            else -> {
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