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


    fun registryConnectedHook() {
        getAgent().getRemotingClient().registryConnectHook {
            getConnectedHook().invoke(getAgent())
        }
    }

    /**
     * Start The Agent client
     */
    override fun run(vararg args: String) {

        val agent = this.getAgent()

        // 1 registryRemotingTask
        this.registryRemotingTask()

        // 2 registryRemotingHandler
        this.registryRemotingHandler()

        // 3 registry connected hook
        this.registryConnectedHook()

        // 4 start agent
        agent.start()
    }

    fun getAgent(): Agent

    fun getConnectedHook(): ConnectedHook = {
        it.invoke(reportExposedInterfacesTask)
        it.invoke(reportClientInfoTask)
    }
}