package restdoc.client.remoting;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.http.ResponseEntity;
import restdoc.client.invoke.HttpInvoker;
import restdoc.remoting.common.body.HttpCommunicationCaptureBody;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSerializable;
import restdoc.remoting.protocol.RemotingSysResponseCode;


@Deprecated
public class HttpTaskRequestProcessor implements NettyRequestProcessor {

    private final HttpInvoker httpInvoker;

    public HttpTaskRequestProcessor(HttpInvoker httpInvoker) {
        this.httpInvoker = httpInvoker;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx,
                                          RemotingCommand request) throws Exception {

        HttpCommunicationCaptureBody capture = RemotingSerializable.decode(request.getBody(),
                HttpCommunicationCaptureBody.class);

        ResponseEntity<Object> responseEntity = httpInvoker.execute(capture);
        capture.setStatus(responseEntity.getStatusCodeValue());
        capture.setResponseHeader(responseEntity.getHeaders());

        if (responseEntity.hasBody()) {
            capture.setResponseBody(responseEntity.getBody());
        }

        RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, "success");
        response.setBody(capture.encode());

        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
