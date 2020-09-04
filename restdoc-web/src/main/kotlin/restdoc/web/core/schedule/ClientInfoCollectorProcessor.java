package restdoc.web.core.schedule;

import io.netty.channel.ChannelHandlerContext;
import restdoc.remoting.ClientChannelInfo;
import restdoc.remoting.common.body.ReportClientInfoRequestBody;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.LanguageCode;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSerializable;
import restdoc.remoting.protocol.RemotingSysResponseCode;

import java.net.InetSocketAddress;

public class ClientInfoCollectorProcessor implements NettyRequestProcessor {

    private ClientManager clientManager;

    public ClientInfoCollectorProcessor(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    /**
     *
     */
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {

        ReportClientInfoRequestBody body =
                RemotingSerializable.decode(request.getBody(), ReportClientInfoRequestBody.class);

        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();

        ClientChannelInfo clientChannelInfo = new ClientChannelInfo(ctx.channel(), address.getAddress().getHostAddress(), LanguageCode.JAVA, 1);
        clientChannelInfo.setHostName(body.getHostname());
        clientChannelInfo.setOsname(body.getOsname());

        clientManager.registerClient(clientChannelInfo.getClientId(), clientChannelInfo);

        return RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null);
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
