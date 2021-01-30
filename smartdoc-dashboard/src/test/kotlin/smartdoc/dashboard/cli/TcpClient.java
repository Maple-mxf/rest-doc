//package restdoc.web.cli;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.util.CharsetUtil;
//import restdoc.core.schedule.HostInfoMessage;
//
//import java.net.InetAddress;
// wxid_eq32udamp8yn22
//
//public class TcpClient {
//
//    public static void main(String[] args) throws Exception {
//        String host = "127.0.0.1";
//        int port = 4321;
//
//        InetAddress inetAddress = InetAddress.getLocalHost();
//
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try {
//            Bootstrap b = new Bootstrap(); // (1)
//            b.group(workerGroup); // (2)
//            b.channel(NioSocketChannel.class); // (3)
//            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
//            b.option(ChannelOption.valueOf("hostName"), inetAddress.getHostName());
//            b.handler(new ChannelInitializer<SocketChannel>() {
//                @Override
//                public void initChannel(SocketChannel ch) throws Exception {
//                    ch.pipeline().addLast(new TimeClientHandler());
//                }
//            });
//
//            // Start the client.
//            ChannelFuture f = b.connect(host, port).sync(); // (5)
//
//            // Wait until the connection is closed.
//            f.channel().closeFuture().sync();
//        } finally {
////            workerGroup.shutdownGracefully();
//        }
//    }
//
//
//    public static class TimeClientHandler extends ChannelInboundHandlerAdapter {
//
//        @Override
//        public void channelActive(ChannelHandlerContext ctx) throws Exception {
//            HostInfoMessage message = new HostInfoMessage();
//            InetAddress localHost = InetAddress.getLocalHost();
//            message.setHostName(localHost.getHostName());
//            message.setLocalIp(localHost.getHostAddress());
//        }
//
//        @Override
//        public void channelRead(ChannelHandlerContext ctx, Object msg) {
//            ByteBuf m = (ByteBuf) msg; // (1)
//            try {
//                /*long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
//                System.err.println(new Date(currentTimeMillis));*/
//
//                CharSequence charSequence = m.readCharSequence(m.readableBytes(), CharsetUtil.UTF_8);
//
//                System.err.println(charSequence);
//
//                // ctx.close();
//            } finally {
//                m.release();
//            }
//        }
//
//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//            cause.printStackTrace();
//            ctx.close();
//        }
//    }
//
//}
