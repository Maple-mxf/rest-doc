package restdoc.client.remoting;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * The RemotingCommandDecoder class provided decode byte array to object
 */
public class RemotingCommandDecoder extends ByteToMessageDecoder {

    /**
     * decode bytes to {@link restdoc.remoting.protocol.RemotingCommand}
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> commands) {
        int length = buf.readableBytes();

        byte[] bytes = new byte[length];
        buf.readBytes(bytes);

//        RemotingCommand command = RemotingCommand.decode(bytes);
//        commands.add(command);

        System.err.println(new String(bytes, CharsetUtil.UTF_8));
    }
}
