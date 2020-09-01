package restdoc.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Global registry
 * Client Tag:  ip ; instanceName;  time
 */
public class ServerRegistryHandler extends SimpleChannelInboundHandler<InstanceTag> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InstanceTag msg) throws Exception {
        Channel channel = ctx.channel();
        ByteBufAllocator allocator = ctx.alloc();
        ByteBuf buffer = allocator.buffer(10);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
