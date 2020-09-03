package restdoc.client.remoting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import restdoc.client.RestDocProperties;
import restdoc.remoting.common.RequestCode;
import restdoc.remoting.netty.NettyClientConfig;
import restdoc.remoting.netty.NettyRemotingClient;


/**
 * The ApplicationClient class provided start client connect to server
 * <p>
 * Establish channel
 */
public class ApplicationClient {

    private static Logger log = LoggerFactory.getLogger(ApplicationClient.class);

    private volatile State state = State.STOPPED;

    private enum State {
        STOPPED,
        RUNNING
    }

    private NettyRemotingClient remotingClient;

    @Autowired
    public ApplicationClient(RestDocProperties restDocProperties,  HttpTaskRequestProcessor httpTaskRequestProcessor ) {

        NettyClientConfig config = new NettyClientConfig();
        config.setUseTLS(false);
        config.setHost(restDocProperties.getServerIp());
        config.setPort(restDocProperties.getServerPort());

        this.remotingClient = new NettyRemotingClient(config);
        this.remotingClient.registerProcessor(RequestCode.SUBMIT_HTTP_PROCESS,
                httpTaskRequestProcessor, null);
    }

    public synchronized void connection() {
        synchronized (this) {
            if (this.state == State.RUNNING) {
                log.error("ApplicationClient already running");
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        remotingClient.start();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
