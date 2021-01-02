package restdoc.client.api;

import restdoc.client.api.exception.DiffVersionException;
import restdoc.remoting.exception.*;
import restdoc.remoting.netty.NettyRemotingClient;
import restdoc.remoting.netty.NettyRequestProcessor;

import java.util.NoSuchElementException;

/**
 * The class Agent.
 *
 * @author Maple
 * @since 1.0.RELEASE
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
     * @return true/false
     * @throws DiffVersionException if console and client version not match,will be throws
     */
    Boolean acknowledgeVersion() throws DiffVersionException, InterruptedException, RemotingConnectException,
            RemotingTimeoutException, RemotingTooMuchRequestException, RemotingSendRequestException;

    /**
     * Invoke Task
     */
    InvokeResult invoke(String taskId) throws NoSuchElementException, InterruptedException, RemotingTooMuchRequestException,
            RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException;

    /**
     * Invoke task
     */
    InvokeResult invoke(RemotingTask remotingTask) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException,
            RemotingConnectException, RemotingTooMuchRequestException;

    /**
     *
     */
    void addTask(RemotingTask task);

    /**
     *
     */
    void addHandler(int code, NettyRequestProcessor handler);
}
