package restdoc.client.api

@Deprecated(message = "AgentStartCallback")
typealias AgentStartCallback = (Agent) -> Unit

@Deprecated(message = "AgentConnectCallback")
typealias AgentConnectCallback = (Agent) -> Unit


typealias ConnectedHook = (Agent) -> Unit


@Deprecated(message = "AgentStartHook")
interface AgentStartHook {

    fun beforeStart(): List<AgentStartCallback>

    fun afterStart(): List<AgentStartCallback>
}

@Deprecated(message = "AgentConnectHook")
interface AgentConnectHook {

    fun beforeConnected(): List<AgentConnectCallback>

    fun afterConnected(): List<AgentConnectCallback>
}

