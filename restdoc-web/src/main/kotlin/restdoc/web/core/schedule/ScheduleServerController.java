package restdoc.web.core.schedule;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import restdoc.remoting.ChannelEventListener;
import restdoc.remoting.ClientChannelInfo;
import restdoc.remoting.common.RequestCode;
import restdoc.remoting.common.body.PostHttpTaskExecuteResultRequestBody;
import restdoc.remoting.common.body.SubmitHttpTaskRequestBody;
import restdoc.remoting.common.header.PostHttpTaskExecuteResultRequestHeader;
import restdoc.remoting.common.header.SubmitHttpTaskRequestHeader;
import restdoc.remoting.exception.RemotingCommandException;
import restdoc.remoting.exception.RemotingSendRequestException;
import restdoc.remoting.exception.RemotingTimeoutException;
import restdoc.remoting.netty.NettyRemotingServer;
import restdoc.remoting.netty.NettyServerConfig;
import restdoc.remoting.protocol.LanguageCode;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSerializable;
import restdoc.remoting.protocol.RemotingSysResponseCode;
import restdoc.web.core.ServiceException;
import restdoc.web.core.Status;


/**
 * ScheduleServer provided the tcp server dashboard
 *
 * @author ubuntu-m
 */
@Component
public class ScheduleServerController implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(ScheduleServerController.class);

    private final NettyRemotingServer remotingServer;

    private final Thread thread;

    private final ClientManager clientManager;

    private final long httpTaskExecuteTimeout = 32 << 9;
    
    @Autowired
    public ScheduleServerController(ScheduleProperties scheduleProperties,
                                    ClientManager clientManager
    ) {
        NettyServerConfig config = new NettyServerConfig();
        config.setListenPort(scheduleProperties.getPort());

        ChannelEventListener eventListener = new ChannelEventListener() {
            @Override
            public void onChannelConnect(String remoteAddr, Channel channel) {

                ScheduleServerController.this.clientManager.registerClient(
                        remoteAddr,
                        new ClientChannelInfo(channel, remoteAddr, LanguageCode.JAVA, 1));
            }

            @Override
            public void onChannelClose(String remoteAddr, Channel channel) {
                ScheduleServerController.this.clientManager.unregisterClient(remoteAddr);
            }

            @Override
            public void onChannelException(String remoteAddr, Channel channel) {
                ScheduleServerController.this.clientManager.unregisterClient(remoteAddr);
            }

            @Override
            public void onChannelIdle(String remoteAddr, Channel channel) {
            }
        };

        this.remotingServer = new NettyRemotingServer(config/*, eventListener*/);
        this.thread = new Thread(ScheduleServerController.this.remotingServer::start);
        this.clientManager = clientManager;
    }

    private void initialize() {
        this.remotingServer.registerProcessor(RequestCode.REPORT_CLIENT_INFO,
                new ClientInfoCollectorProcessor(clientManager), null);
    }

    @Override
    public void run(String... args) {
        this.initialize();
        this.thread.start();
        log.info("ScheduleServerController started");
    }

    public HttpTaskData syncSubmitRemoteHttpTask(String clientId,
                                                 String taskId,
                                                 SubmitHttpTaskRequestBody body)
            throws InterruptedException,
            RemotingTimeoutException,
            RemotingSendRequestException,
            RemotingCommandException {

        SubmitHttpTaskRequestHeader header = new SubmitHttpTaskRequestHeader();
        header.setTaskId(taskId);

        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.SUBMIT_HTTP_PROCESS, header);
        request.setBody(body.encode());

        ClientChannelInfo clientChannelInfo = this.clientManager.findClient(clientId);
        RemotingCommand response = remotingServer.invokeSync(clientChannelInfo.getChannel(), request,
                this.httpTaskExecuteTimeout);

        if (response.getCode() == RemotingSysResponseCode.SUCCESS) {

            PostHttpTaskExecuteResultRequestBody responseBody = RemotingSerializable.decode(response.getBody(),
                    PostHttpTaskExecuteResultRequestBody.class);

            PostHttpTaskExecuteResultRequestHeader responseHeader =
                    (PostHttpTaskExecuteResultRequestHeader) response.decodeCommandCustomHeader(PostHttpTaskExecuteResultRequestHeader.class);

            return new HttpTaskData(
                    responseBody.getStatus(),
                    responseBody.getResponseHeader(),
                    responseBody.getResponseBody(),
                    responseHeader.getTaskId());
        } else {
            throw new ServiceException(response.getRemark(), Status.INTERNAL_SERVER_ERROR);
        }
    }
}
