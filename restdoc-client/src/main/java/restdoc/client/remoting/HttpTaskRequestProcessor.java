package restdoc.client.remoting;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.http.ResponseEntity;
import restdoc.client.executor.HttpTaskExecutor;
import restdoc.remoting.CommandCustomHeader;
import restdoc.remoting.common.body.PostHttpTaskExecuteResultRequestBody;
import restdoc.remoting.common.body.SubmitHttpTaskRequestBody;
import restdoc.remoting.common.header.SubmitHttpTaskRequestHeader;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSerializable;
import restdoc.remoting.protocol.RemotingSysResponseCode;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;


public class HttpTaskRequestProcessor implements NettyRequestProcessor {

    private final HttpTaskExecutor httpTaskExecutor;


    public HttpTaskRequestProcessor(HttpTaskExecutor httpTaskExecutor) {
        this.httpTaskExecutor = httpTaskExecutor;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx,
                                          RemotingCommand request) throws Exception {

        SubmitHttpTaskRequestBody requestBody = RemotingSerializable.decode(request.getBody(),
                SubmitHttpTaskRequestBody.class);

        CommandCustomHeader requestHeader = request.decodeCommandCustomHeader(SubmitHttpTaskRequestHeader.class);

        ResponseEntity<Object> responseEntity = httpTaskExecutor.execute(requestBody);

        Map<String, String> responseHeader = responseEntity.getHeaders()
                .entrySet()
                .stream()
                .map(t -> new AbstractMap.SimpleEntry<>(t.getKey(), String.join(",", t.getValue())))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        PostHttpTaskExecuteResultRequestBody body
                = new PostHttpTaskExecuteResultRequestBody();

        body.setStatus(responseEntity.getStatusCodeValue());
        body.setResponseHeader(responseHeader);
        body.setResponseBody(responseEntity.getBody());

        RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, "success");
        response.setBody(body.encode());
        response.writeCustomHeader(requestHeader);

        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
