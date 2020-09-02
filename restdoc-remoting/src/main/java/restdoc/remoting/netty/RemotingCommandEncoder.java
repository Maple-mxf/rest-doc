package restdoc.remoting.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import restdoc.remoting.protocol.RemotingCommand;

import java.nio.ByteBuffer;


/**
 * The RemotingCommandEncoder class provided encode out remoting message
 * <p>
 * or implement {@link io.netty.handler.codec.MessageToByteEncoder}
 *
 * @author ubuntu-m
 */
public class RemotingCommandEncoder extends MessageToByteEncoder<RemotingCommand> {

    @Override
    public void encode(ChannelHandlerContext ctx, RemotingCommand remotingCommand, ByteBuf out) {
        try {
            ByteBuffer header = remotingCommand.encodeHeader();
            out.writeBytes(header);
            byte[] body = remotingCommand.getBody();
            if (body != null) {
                out.writeBytes(body);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
