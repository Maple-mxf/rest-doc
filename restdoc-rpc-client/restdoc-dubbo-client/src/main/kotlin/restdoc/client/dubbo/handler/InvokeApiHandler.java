package restdoc.client.dubbo.handler;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restdoc.client.api.model.DubboInvocation;
import restdoc.client.api.model.InvocationResult;
import restdoc.client.dubbo.DubboInvokerImpl;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSerializable;
import restdoc.remoting.protocol.RemotingSysResponseCode;

/**
 * The class InvokerDubboAPIHandler
 *
 * @author Maple
 */
@Component
public class InvokeApiHandler implements NettyRequestProcessor {

    private final DubboInvokerImpl invoker;

    @Autowired
    public InvokeApiHandler(DubboInvokerImpl invoker) {
        this.invoker = invoker;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        DubboInvocation invocation = RemotingSerializable.decode(request.getBody(), DubboInvocation.class);
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
