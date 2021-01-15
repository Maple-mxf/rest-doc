package restdoc.client.restweb.handler;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import restdoc.client.api.model.InvocationResult;
import restdoc.client.api.model.HttpInvocation;
import restdoc.client.restweb.RestWebInvokerImpl;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSerializable;
import restdoc.remoting.protocol.RemotingSysResponseCode;

/**
 * InvokerAPIHandler
 */
public class InvokerApiHandler implements NettyRequestProcessor {

    private final RestWebInvokerImpl invoker;

    @Autowired
    public InvokerApiHandler(RestWebInvokerImpl invoker) {
        this.invoker = invoker;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        HttpInvocation invocation = RemotingSerializable.decode(request.getBody(), HttpInvocation.class);
        InvocationResult invocationResult = invoker.rpcInvoke(invocation);
        RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null);
        response.setBody(invocationResult.encode());
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
