package smartdoc.dashboard.schedule

import restdoc.remoting.InvokeCallback
import restdoc.remoting.protocol.RemotingCommand

class RemotingTaskWrapper {
    var command: RemotingCommand? = null
    var responseType: Class<out Any>? = null
    var timeoutMills: Long = 3000L
    var async: Boolean = false
    var callback: InvokeCallback = InvokeCallback {}
}