package restdoc.core.schedule;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The ScheduleInstanceServerHandler class provided cache each client channel
 * and read ByteBuf/ write ByteBuf
 * <p>
 * Read And Write ByteBuf format by json string
 * <p>
 * code sample:
 * <pre>
 *     {@code
 *       ByteBuf buf = Unpooled.buffer();
 *       buf.writeCharSequence("",CharsetUtil.UTF_8);
 *     }
 * </pre>
 *
 * @author ubuntu-m
 */
@Component
public class ScheduleInstanceServerHandler extends ChannelInboundHandlerAdapter {

    private static Logger log = LoggerFactory.getLogger(ScheduleInstanceServerHandler.class);

    /**
     * ChannelGroup is channel collection. thread safe
     */
    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channels.add(ctx.channel());

        ChannelId id = ctx.channel().id();
        log.info("Channel {} connected", id);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        CharSequence sequence = buf.readCharSequence(buf.readableBytes(), CharsetUtil.UTF_8);
    }
}
