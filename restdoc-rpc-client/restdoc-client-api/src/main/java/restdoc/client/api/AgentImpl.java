package restdoc.client.api;

import io.netty.channel.Channel;
import restdoc.client.api.exception.DiffVersionException;
import restdoc.client.api.model.Version;
import restdoc.remoting.ChannelEventListener;
import restdoc.remoting.common.RequestCode;
import restdoc.remoting.exception.*;
import restdoc.remoting.netty.NettyClientConfig;
import restdoc.remoting.netty.NettyRemotingClient;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSerializable;
import restdoc.remoting.protocol.RemotingSysResponseCode;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

import static restdoc.remoting.protocol.RemotingCommand.createRequestCommand;

/**
 * The class AgentImpl default implement agent
 * Provide remoting client and config
 *
 * @author Maple
 * @since 2.0.RELEASE
 */
public class AgentImpl implements Agent {

    private final ServerProperties serverProperties;

    private final NettyRemotingClient remotingClient;

    private final Status status = Status.STARTED;

    private final CopyOnWriteArrayList<RemotingTask> remotingTasks = new CopyOnWriteArrayList<>();

    private final String ackVersionTaskId = "ackVersion";

    public AgentImpl(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
        RemotingTask acknowledgeVersionTask = new RemotingTask(ackVersionTaskId,
                RemotingTaskType.SYNC, createRequestCommand(RequestCode.AcknowledgeVersion, null),
                3000, null);
        this.addTask(acknowledgeVersionTask);

        NettyClientConfig config = new NettyClientConfig();
        config.setHost(serverProperties.host());
        config.setPort(serverProperties.port());

        ChannelEventListener channelEventListener =
                new ChannelEventListener() {
                    @Override
                    public void onChannelConnect(String remoteAddr, Channel channel) {
                    }

                    @Override
                    public void onChannelClose(String remoteAddr, Channel channel) {
                    }

                    @Override
                    public void onChannelException(String remoteAddr, Channel channel, Throwable cause) {
                    }

                    @Override
                    public void onChannelIdle(String remoteAddr, Channel channel) {
                    }
                };

        this.remotingClient = new NettyRemotingClient(config, channelEventListener);
    }

    @Override
    public NettyRemotingClient getRemotingClient() {
        return this.remotingClient;
    }

    @Override
    public void start() throws RemotingException {
        this.remotingClient.start();
    }

    @Override
    public Status getClientStatus() {
        return this.status;
    }

    @Override
    public String getServerRemoteAddress() {
        return this.serverProperties.host() + ":" + this.serverProperties.port();
    }

    @Override
    public Boolean acknowledgeVersion() throws DiffVersionException, InterruptedException,
            RemotingConnectException, RemotingTimeoutException,
            RemotingTooMuchRequestException, RemotingSendRequestException {
        InvokeResult invokeResult = this.invoke(ackVersionTaskId);
        RemotingCommand response;
        if (invokeResult != null && (response = invokeResult.getResponse()) != null
                && RemotingSysResponseCode.SUCCESS == response.getCode()) {
            Version version = RemotingSerializable.decode(response.getBody(), Version.class);

            if (!ClientAgentVersion.getCurrentVersion().equals(version.getVersion()))
            {
                throw new DiffVersionException();
            }
        }
        return true;
    }

    @Override
    public InvokeResult invoke(String taskId) throws NoSuchElementException,
            InterruptedException,
            RemotingTooMuchRequestException,
            RemotingTimeoutException,
            RemotingSendRequestException,
            RemotingConnectException {
        RemotingTask remotingTask = remotingTasks.stream().filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("NoSuch Task id %s", taskId)));
        return this.invoke(remotingTask);
    }

    @Override
    public InvokeResult invoke(RemotingTask remotingTask) throws InterruptedException,
            RemotingTimeoutException,
            RemotingSendRequestException,
            RemotingConnectException,
            RemotingTooMuchRequestException {

        switch (remotingTask.getType()) {
            case SYNC: {
                RemotingCommand response = this.getRemotingClient()
                        .invokeSync(this.getServerRemoteAddress(), remotingTask.getRequest(), remotingTask.getTimeoutMills());
                return new InvokeResult(response, new Date().getTime());
            }
            case ASYNC: {
                this.getRemotingClient().invokeAsync(
                        this.getServerRemoteAddress(),
                        remotingTask.getRequest(), remotingTask.getTimeoutMills(), remotingTask.getInvokeCallback());
                return new InvokeResult(null, new Date().getTime());
            }
            case ONE_WAY: {
                this.getRemotingClient().invokeOneway(
                        this.getServerRemoteAddress(),
                        remotingTask.getRequest(),
                        remotingTask.getTimeoutMills()
                );
                return new InvokeResult(null, new Date().getTime());
            }
        }
        return null;
    }

    @Override
    public void addTask(RemotingTask task) {
        this.remotingTasks.add(task);
    }

    @Override
    public void addHandler(int code, NettyRequestProcessor handler) {
        remotingClient.registerProcessor(code, handler, null);
    }
}
