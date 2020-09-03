package restdoc.web.core.schedule;

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
import restdoc.remoting.netty.RemotingCommandDecoder;
import restdoc.remoting.netty.RemotingCommandEncoder;


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

    private final
    RemotingCommandDecoder remotingCommandDecoder;

    private final
    RemotingCommandEncoder remotingCommandEncoder;

    public ScheduleServer(ScheduleProperties scheduleProperties,
                          ScheduleInstanceServerHandler scheduleInstanceServerHandler, RemotingCommandDecoder remotingCommandDecoder, RemotingCommandEncoder remotingCommandEncoder) {
        this.scheduleProperties = scheduleProperties;
        this.scheduleInstanceServerHandler = scheduleInstanceServerHandler;
        this.remotingCommandDecoder = remotingCommandDecoder;
        this.remotingCommandEncoder = remotingCommandEncoder;
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
                            ch.pipeline().addLast(remotingCommandDecoder);
                            ch.pipeline().addLast(remotingCommandEncoder);

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(scheduleProperties.getPort()).sync(); // (7)

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
        new Thread(this::runTcpServer).start();

        log.info("ScheduleServer completed");
    }
}
