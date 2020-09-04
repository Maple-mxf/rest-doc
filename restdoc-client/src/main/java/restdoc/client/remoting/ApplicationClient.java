package restdoc.client.remoting;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import restdoc.client.RestDocProperties;
import restdoc.remoting.ChannelEventListener;
import restdoc.remoting.common.RemotingUtil;
import restdoc.remoting.common.RequestCode;
import restdoc.remoting.common.body.ReportClientInfoRequestBody;
import restdoc.remoting.exception.RemotingConnectException;
import restdoc.remoting.exception.RemotingSendRequestException;
import restdoc.remoting.exception.RemotingTimeoutException;
import restdoc.remoting.exception.RemotingTooMuchRequestException;
import restdoc.remoting.netty.NettyClientConfig;
import restdoc.remoting.netty.NettyRemotingClient;
import restdoc.remoting.protocol.RemotingCommand;


/**
 * The ApplicationClient class provided start client connect to server
 * <p>
 * Establish channel
 */
public class ApplicationClient {

    private static Logger log = LoggerFactory.getLogger(ApplicationClient.class);

    private volatile State state = State.STOPPED;

    private enum State {
        STOPPED,
        RUNNING
    }

    private NettyRemotingClient remotingClient;

    @Autowired
    public ApplicationClient(RestDocProperties restDocProperties, HttpTaskRequestProcessor httpTaskRequestProcessor) {

        NettyClientConfig config = new NettyClientConfig();
        config.setUseTLS(false);
        config.setHost(restDocProperties.getServerIp());
        config.setPort(restDocProperties.getServerPort());


        ChannelEventListener eventListener = new ChannelEventListener() {
            @Override
            public void onChannelConnect(String remoteAddr, Channel channel) throws InterruptedException,
                    RemotingSendRequestException, RemotingTimeoutException, RemotingTooMuchRequestException, RemotingConnectException {

                RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.REPORT_CLIENT_INFO, null);
                ReportClientInfoRequestBody body = new ReportClientInfoRequestBody();
                body.setOsname(System.getProperty("os.name"));
                body.setHostname(RemotingUtil.getHostname());
                request.setBody(body.encode());

                ApplicationClient.this.remotingClient.invokeAsync(
                        remoteAddr,
                        request,
                        3000L,
                        responseFuture -> {
                            RemotingCommand response = responseFuture.waitResponse(3000);
                            log.info("register client info response code {} ", response);
                        });
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

        this.remotingClient = new NettyRemotingClient(config, eventListener);
        this.remotingClient.registerProcessor(RequestCode.SUBMIT_HTTP_PROCESS,
                httpTaskRequestProcessor, null);

    }

    public synchronized void connection() {
        synchronized (this) {
            if (this.state == State.RUNNING) {
                log.error("ApplicationClient already running");
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        remotingClient.start();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
