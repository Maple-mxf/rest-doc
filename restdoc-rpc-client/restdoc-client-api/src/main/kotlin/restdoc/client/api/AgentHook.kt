package restdoc.client.api

typealias AgentCallback = (Agent) -> Unit

interface AgentHook {

    fun beforeStart(): List<AgentCallback>

    fun afterStart(): List<AgentCallback>
}