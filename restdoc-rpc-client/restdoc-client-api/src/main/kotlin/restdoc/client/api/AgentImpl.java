package restdoc.client.api;

import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import restdoc.remoting.ChannelEventListener;
import restdoc.remoting.exception.*;
import restdoc.remoting.netty.NettyClientConfig;
import restdoc.remoting.netty.NettyRemotingClient;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

public class AgentImpl implements Agent {

    private final AgentConfigurationProperties agentConfigurationProperties;

    private final NettyRemotingClient remotingClient;

    private final Status status = Status.STARTED;

    private final CopyOnWriteArrayList<RemotingTask> remotingTasks = new CopyOnWriteArrayList<>();

    @Autowired
    public AgentImpl(AgentConfigurationProperties agentConfigurationProperties) {
        this.agentConfigurationProperties = agentConfigurationProperties;

        NettyClientConfig config = new NettyClientConfig();
        config.setHost(agentConfigurationProperties.getHost());
        config.setPort(agentConfigurationProperties.getPort());

        ChannelEventListener channelEventListener =
                new ChannelEventListener() {
                    @Override
                    public void onChannelConnect(String remoteAddr, Channel channel) throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException, RemotingTooMuchRequestException, RemotingConnectException {
                    }

                    @Override
                    public void onChannelClose(String remoteAddr, Channel channel) {
                    }

                    @Override
                    public void onChannelException(String remoteAddr, Channel channel) {
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
        return this.agentConfigurationProperties.getHost() + ":" + this.agentConfigurationProperties.getPort();
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
