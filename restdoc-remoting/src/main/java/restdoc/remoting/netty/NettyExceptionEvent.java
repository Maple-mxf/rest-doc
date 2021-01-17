package restdoc.remoting.netty;

import io.netty.channel.Channel;

public class NettyExceptionEvent extends NettyEvent {

    private Throwable cause;

    public NettyExceptionEvent(NettyEventType type, String remoteAddr, Channel channel, Throwable cause) {
        super(type, remoteAddr, channel);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
