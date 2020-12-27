package restdoc.client.api;

import restdoc.remoting.exception.*;
import restdoc.remoting.netty.NettyRemotingClient;
import restdoc.remoting.netty.NettyRequestProcessor;

import java.util.NoSuchElementException;


/**
 * The class Agent
 *
 * @author Maple
 */
public interface Agent {

    /**
     * Get remoting client
     */
    NettyRemotingClient getRemotingClient();

    /**
     * Start client channel
     */
    void start() throws RemotingException;

    /**
     * Get client server status
     */
    Status getClientStatus();

    /**
     * Get ServerRemoteAddress
     */
    String getServerRemoteAddress();

    /**
     * Invoke Task
     */
    InvokeResult invoke(String taskId) throws NoSuchElementException, InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException;

    /**
     * Invoke task
     */
    InvokeResult invoke(RemotingTask remotingTask) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException, RemotingTooMuchRequestException;

    /**
     *
     */
    void addTask(RemotingTask task);

    /**
     *
     */
    void addHandler(int code, NettyRequestProcessor handler);
}
