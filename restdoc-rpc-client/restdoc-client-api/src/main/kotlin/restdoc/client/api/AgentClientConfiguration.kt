package restdoc.client.api

import org.springframework.boot.CommandLineRunner

interface AgentClientConfiguration : CommandLineRunner {

    /**
     * Registry RemotingTask
     */
    fun registryRemotingTask()

    /**
     * Request process handelr
     */
    fun registryRemotingHandler()


    fun registryConnectedHook()

    /**
     * Start The Agent client
     */
    override fun run(vararg args: String) {

        val agent = this.getAgent()
        val hook = this.hook()

        hook.beforeStart().forEach { it.invoke(agent) }

        // 1 registryRemotingTask
        this.registryRemotingTask()

        // 2 registryRemotingHandler
        this.registryRemotingHandler()

        // 3 registry connected hook
        this.registryConnectedHook()

        // 4 start agent
        agent.start()

        hook.afterStart().forEach { it.invoke(agent) }
    }

    fun getAgent(): Agent

    /**
     *
     */
    @Deprecated(message = "")
    fun hook(): AgentStartHook

    fun getConnectedHook(): ConnectedHook = {
        it.invoke(reportExposedInterfacesTask)
        it.invoke(reportClientInfoTask)
    }
}