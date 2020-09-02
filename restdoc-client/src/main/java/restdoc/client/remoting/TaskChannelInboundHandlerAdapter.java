package restdoc.client.remoting;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import restdoc.remoting.protocol.RemotingCommand;


/**
 * The TaskChannelInboundHandlerAdapter class receive task and execute
 */
public class TaskChannelInboundHandlerAdapter extends SimpleChannelInboundHandler<RemotingCommand> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) {

        byte[] body = msg.getBody();
    }
}
