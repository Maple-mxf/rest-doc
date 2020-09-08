package restdoc.client.remoting;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import restdoc.client.context.EndpointsListener;
import restdoc.remoting.common.body.GetClientApiListRequestBody;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSysResponseCode;

public class PostEmptyApiTemplateRequestProcessor implements NettyRequestProcessor {

    private final EndpointsListener endpointsListener;

    @Autowired
    public PostEmptyApiTemplateRequestProcessor(EndpointsListener endpointsListener) {
        this.endpointsListener = endpointsListener;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS,
                "OK");

        GetClientApiListRequestBody body = new GetClientApiListRequestBody();
        body.setApiEmptyTemplates(endpointsListener.getEmptyApiTemplates());

        response.setBody(body.encode());

        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
