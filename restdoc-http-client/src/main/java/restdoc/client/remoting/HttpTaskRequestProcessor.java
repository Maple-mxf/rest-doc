package restdoc.client.remoting;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.http.ResponseEntity;
import restdoc.client.executor.HttpTaskExecutor;
import restdoc.remoting.common.body.HttpCommunicationCapture;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSerializable;
import restdoc.remoting.protocol.RemotingSysResponseCode;


public class HttpTaskRequestProcessor implements NettyRequestProcessor {

    private final HttpTaskExecutor httpTaskExecutor;

    public HttpTaskRequestProcessor(HttpTaskExecutor httpTaskExecutor) {
        this.httpTaskExecutor = httpTaskExecutor;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx,
                                          RemotingCommand request) throws Exception {

        HttpCommunicationCapture capture = RemotingSerializable.decode(request.getBody(),
                HttpCommunicationCapture.class);

        ResponseEntity<Object> responseEntity = httpTaskExecutor.execute(capture);
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
