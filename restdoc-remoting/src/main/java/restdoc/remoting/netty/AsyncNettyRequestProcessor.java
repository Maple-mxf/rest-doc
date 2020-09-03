
package restdoc.remoting.netty;

import io.netty.channel.ChannelHandlerContext;
import restdoc.remoting.protocol.RemotingCommand;

public abstract class AsyncNettyRequestProcessor implements NettyRequestProcessor {

    public void asyncProcessRequest(ChannelHandlerContext ctx, RemotingCommand request, RemotingResponseCallback responseCallback) throws Exception {
        RemotingCommand response = processRequest(ctx, request);
        responseCallback.callback(response);
    }
}
