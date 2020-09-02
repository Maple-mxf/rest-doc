package restdoc.core.schedule;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * ScheduleServer provided the tcp server dashboard
 *
 * @author ubuntu-m
 */
@Component
public class ScheduleServer implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(ScheduleServer.class);

    final
    ScheduleProperties scheduleProperties;

    final
    ScheduleInstanceServerHandler scheduleInstanceServerHandler;

    public ScheduleServer(ScheduleProperties scheduleProperties,
                          ScheduleInstanceServerHandler scheduleInstanceServerHandler) {
        this.scheduleProperties = scheduleProperties;
        this.scheduleInstanceServerHandler = scheduleInstanceServerHandler;
    }

    private void runTcpServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(scheduleInstanceServerHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(scheduleProperties.getPort()).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void run(String... args) {
        log.info("ScheduleServer loading");

        // Create new thread running tcpServer
        new Thread(new Runnable() {
            @Override
            public void run() {
                runTcpServer();
            }
        }).start();

        log.info("ScheduleServer completed");
    }
}
