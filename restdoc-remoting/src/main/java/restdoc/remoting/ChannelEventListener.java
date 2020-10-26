package restdoc.remoting;

import io.netty.channel.Channel;
import restdoc.remoting.exception.RemotingConnectException;
import restdoc.remoting.exception.RemotingSendRequestException;
import restdoc.remoting.exception.RemotingTimeoutException;
import restdoc.remoting.exception.RemotingTooMuchRequestException;

public interface ChannelEventListener {
    void onChannelConnect(final String remoteAddr, final Channel channel)
            throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException,
            RemotingTooMuchRequestException, RemotingConnectException;

    void onChannelClose(final String remoteAddr, final Channel channel);

    void onChannelException(final String remoteAddr, final Channel channel);

    void onChannelIdle(final String remoteAddr, final Channel channel);
}
