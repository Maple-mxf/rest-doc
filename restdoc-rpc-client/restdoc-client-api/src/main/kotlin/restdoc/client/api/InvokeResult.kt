package restdoc.client.api

import restdoc.remoting.protocol.RemotingCommand
import java.util.*

data class InvokeResult(val response: RemotingCommand?, val completeTime: Long = Date().time)

fun empty() = InvokeResult(null)