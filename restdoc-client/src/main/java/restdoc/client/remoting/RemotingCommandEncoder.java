package restdoc.client.remoting;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSerializable;


/**
 * The RemotingCommandEncoder class provided encode out remoting message
 * <p>
 * or implement {@link io.netty.handler.codec.MessageToByteEncoder}
 *
 * @author ubuntu-m
 */
public class RemotingCommandEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object message, ChannelPromise promise) {
        RemotingCommand command = (RemotingCommand) message;
        ByteBuf encoded = ctx.alloc().buffer();
        encoded.writeBytes(RemotingSerializable.encode(command));

        ctx.write(encoded, promise);
    }
}
