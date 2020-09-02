package restdoc.client.remoting;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.stereotype.Component;
import restdoc.remoting.protocol.RemotingCommand;

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
        int readableBytes = buf.readableBytes();
        byte[] bytes = buf.readBytes(readableBytes).array();

        RemotingCommand command = RemotingCommand.decode(bytes);
        commands.add(command);
    }
}
