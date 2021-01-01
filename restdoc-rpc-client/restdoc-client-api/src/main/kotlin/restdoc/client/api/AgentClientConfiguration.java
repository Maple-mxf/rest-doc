package restdoc.client.api;

import org.springframework.boot.CommandLineRunner;
import restdoc.client.api.exception.DiffVersionException;
import restdoc.remoting.common.RequestCode;
import restdoc.remoting.exception.RemotingException;
import restdoc.remoting.netty.NettyRequestProcessor;


/**
 * The class AgentClientConfiguration
 *
 * @author Maple
 */
public interface AgentClientConfiguration extends CommandLineRunner {

    /**
     * Registry RemotingTask
     */
    default void registryRemotingTask() {
    }

    /**
     * Request process handelr
     */
    default void registryRemotingHandler() {
        getAgent().addHandler(RequestCode.GET_CLIENT_INFO, getReportClientInfoHandler());
        getAgent().addHandler(RequestCode.GET_EXPOSED_API, getExportAPIHandler());
        getAgent().addHandler(RequestCode.INVOKE_API, getInvokeAPIHandler());
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


    default void run(String... args) throws RemotingException, DiffVersionException, InterruptedException {

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

    Agent getAgent();
}
