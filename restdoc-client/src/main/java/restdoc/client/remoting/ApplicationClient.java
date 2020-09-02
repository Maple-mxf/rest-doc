package restdoc.client.remoting;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restdoc.client.RestDocProperties;


/**
 * The ApplicationClient class provided start client connect to server
 * <p>
 * Establish channel
 */
public class ApplicationClient {

    private final RestDocProperties restDocProperties;

    private static Logger log = LoggerFactory.getLogger(ApplicationClient.class);

    private State state = State.STOPPED;

    private enum State {
        STOPPED,
        RUNNING
    }

    public ApplicationClient(RestDocProperties restDocProperties) {
        this.restDocProperties = restDocProperties;
    }

    public synchronized void connection() {
        synchronized (this) {
            if (this.state == State.RUNNING) {
                log.error("ApplicationClient already running");
                return;
            }

            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                Bootstrap b = new Bootstrap(); // (1)
                b.group(workerGroup); // (2)
                b.channel(NioSocketChannel.class); // (3)
                b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
                b.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {

                    }
                });

                // Start the client.
                ChannelFuture f = b.connect(restDocProperties.getServerIp(),
                        restDocProperties.getServerPort()).sync();

                // Wait until the connection is closed.
                f.channel().closeFuture().sync();

                this.state = State.RUNNING;

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        }
    }
}
