package restdoc.web.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSerializable;
import restdoc.remoting.protocol.RemotingSysResponseCode;

import java.util.Map;
import java.util.concurrent.Callable;

public class TcpServerHandler extends ChannelInboundHandlerAdapter {

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    ObjectMapper mapper = new ObjectMapper();

    static {
//        init();
    }

    public static void init() {
        GlobalEventExecutor.INSTANCE
                .submit((Callable<Void>) () -> {
                    for (int i = 0; i < 10000; i++) {
                        if (!channels.isEmpty()) {
                            for (Channel channel : channels) {
                                ByteBuf byteBuf = Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8);
                                ChannelFuture future = channel.writeAndFlush(byteBuf);
                                future.addListener(future1 -> System.err.println("completed"));
                            }
                        }
                        Thread.sleep(1000);
                    }
                    return null;
                });
    }


    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws JsonProcessingException { // (1)

        channels.add(ctx.channel());
        ChannelConfig config = ctx.channel().config();

        Map<ChannelOption<?>, Object> options = config.getOptions();
        options.forEach((option, value) -> System.err.println(String.format("%s %s %s", option.id(), option.name(), value)));


        byte[] bytes = mapper.writeValueAsString(ImmutableMap.of("k", "v"))
                .getBytes(CharsetUtil.UTF_8);

        RemotingCommand remotingCommand = RemotingCommand.createRequestCommand(RemotingSysResponseCode.SUCCESS, () -> {
        });
        remotingCommand.setCode(1);
        remotingCommand.setVersion(1);
        remotingCommand.setBody(bytes);

        ByteBuf byteBuf = Unpooled.buffer().writeBytes(RemotingSerializable.toJson(remotingCommand).getBytes(CharsetUtil.UTF_8));
        ctx.channel().writeAndFlush(byteBuf);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /*@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        channels.writeAndFlush("server response", ChannelMatchers.is(ctx.channel()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }*/

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TcpServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(4321).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new TcpServerHandler().run();
    }
}
