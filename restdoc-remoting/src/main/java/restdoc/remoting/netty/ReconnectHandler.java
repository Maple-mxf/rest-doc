package restdoc.remoting.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import restdoc.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 * ReconnectHandler
 *
 * @author Overman
 */
@ChannelHandler.Sharable
public class ReconnectHandler extends ChannelInboundHandlerAdapter {

    private int retryCount = 0;

    private int maxRetryCount = 1000;

    private NettyRemotingClient remotingClient;

    public ReconnectHandler(NettyRemotingClient remotingClient) {
        this.remotingClient = remotingClient;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().eventLoop()
                .schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            remotingClient.reconnect();
                        } catch (RemotingException e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000, TimeUnit.MILLISECONDS);

        ctx.fireChannelInactive();
    }
}
