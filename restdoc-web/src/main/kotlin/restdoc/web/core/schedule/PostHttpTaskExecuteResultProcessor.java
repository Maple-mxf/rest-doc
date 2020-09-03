package restdoc.web.core.schedule;

import io.netty.channel.ChannelHandlerContext;
import restdoc.remoting.common.body.PostHttpTaskExecuteResultRequestBody;
import restdoc.remoting.netty.AsyncNettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSerializable;
import restdoc.remoting.protocol.RemotingSysResponseCode;

import java.util.Map;

/**
 * @author ubuntu-m
 */
@Deprecated
public class PostHttpTaskExecuteResultProcessor extends AsyncNettyRequestProcessor {

    private ScheduleServerController scheduleServerController;

    public PostHttpTaskExecuteResultProcessor(final ScheduleServerController controller) {
        this.scheduleServerController = controller;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {

        PostHttpTaskExecuteResultRequestBody body = RemotingSerializable.decode(request.getBody(),
                PostHttpTaskExecuteResultRequestBody.class);

        int httpStatus = body.getStatus();
        Map<String, Object> httpResponseHeader = body.getResponseHeader();
        Object responseBody = body.getResponseBody();

        return RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null);
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
