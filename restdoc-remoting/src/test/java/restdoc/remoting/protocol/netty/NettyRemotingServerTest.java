package restdoc.remoting.protocol.netty;

import org.junit.Test;
import restdoc.remoting.netty.NettyRemotingServer;
import restdoc.remoting.netty.NettyServerConfig;

public class NettyRemotingServerTest {

    @Test
    public void testStartServer() {

        NettyServerConfig config = new NettyServerConfig();
        config.setListenPort(4321);

        NettyRemotingServer server = new NettyRemotingServer(config);
        server.start();
    }

}
