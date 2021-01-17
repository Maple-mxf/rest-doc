package restdoc.client.api;

import restdoc.client.api.exception.DiffVersionException;
import restdoc.remoting.common.RequestCode;
import restdoc.remoting.exception.RemotingException;
import restdoc.remoting.netty.NettyRequestProcessor;


/**
 * The class AgentClientConfiguration
 * <p>
 * SPI Interface
 * <p>
 * classpath/META-INF/restdoc.client.api.AgentClientConfiguration
 *
 * @author Maple
 * @see java.util.ServiceLoader
 * @see SPI
 */
@SPI(name = "restdoc.client.api.AgentClientConfiguration")
public interface AgentClientConfiguration {

    /**
     * @return module name
     */
    String module();

    /**
     * Registry RemotingTask
     */
    default void registryRemotingTask() {
    }

    /**
     * Request process handelr
     */
    default void registryRemotingHandler() {
        getAgent().addHandler(RequestCode.CollectClientInfo, getReportClientInfoHandler());
        getAgent().addHandler(RequestCode.CollectApi, getExportAPIHandler());
        getAgent().addHandler(RequestCode.InvokeApi, getInvokeAPIHandler());
    }

    /**
     *
     */
    NettyRequestProcessor getInvokeAPIHandler();

    /**
     *
     */
    NettyRequestProcessor getReportClientInfoHandler();

    /**
     *
     */
    NettyRequestProcessor getExportAPIHandler();


    /**
     * After started hook call
     */
    default void start() throws RemotingException, DiffVersionException, InterruptedException {

        Agent agent = this.getAgent();

        // 1 registryRemotingTask
        this.registryRemotingTask();

        // 2 registryRemotingHandler
        this.registryRemotingHandler();

        // 3 start agent
        agent.start();

        // 4 Check version
        if (!agent.acknowledgeVersion()) throw new RemotingException("Version ack failed");
    }

    /**
     * @return Agent instance
     * @see AgentClientConfiguration#getAgent(ServerProperties)
     */
    Agent getAgent();

    @Deprecated
    default Agent getAgent(ServerProperties serverProperties) {
        synchronized (this) {
            if (ContextHolder.agent == null)
                ContextHolder.agent = new AgentImpl(serverProperties);
            return ContextHolder.agent;
        }
    }
}
