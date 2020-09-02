package restdoc.client.remoting;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import restdoc.remoting.protocol.RemotingCommand;


/**
 * The TaskChannelInboundHandlerAdapter class receive task and execute
 */
public class TaskChannelInboundHandlerAdapter extends SimpleChannelInboundHandler<RemotingCommand> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) {
        System.err.println(msg);
        System.err.println(new String(msg.getBody(), CharsetUtil.UTF_8));
    }
}
