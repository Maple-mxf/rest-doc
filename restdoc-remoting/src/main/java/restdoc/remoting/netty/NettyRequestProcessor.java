package restdoc.remoting.netty;

import io.netty.channel.ChannelHandlerContext;
import restdoc.remoting.protocol.RemotingCommand;

public interface NettyRequestProcessor {

    RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request)
            throws Exception;

    boolean rejectRequest();

}
