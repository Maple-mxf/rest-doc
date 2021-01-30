package smartdoc.dashboard.schedule

import org.springframework.stereotype.Component
import restdoc.remoting.common.RequestCode
import restdoc.remoting.protocol.RemotingCommand
import java.util.concurrent.ConcurrentHashMap

@Component("taskHolder")
open class RemotingTaskHolder {

    private val task: ConcurrentHashMap<Int, RemotingTaskWrapper> = ConcurrentHashMap()

    init {
        val collectApiTask = RemotingTaskWrapper()
        collectApiTask.apply {
            this.command = RemotingCommand.createRequestCommand(RequestCode.CollectApi, null)
            this.async = false
//            this.responseType =
        }
    }

    /*fun get(code: Int): RemotingTaskWrapper {

    }*/
}