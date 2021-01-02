package restdoc.client.restweb.handler;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import restdoc.client.api.AgentConfigurationProperties;
import restdoc.client.api.model.ClientInfo;
import restdoc.remoting.common.RemotingUtil;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSysResponseCode;
import restdoc.rpc.client.common.model.ApplicationType;

import java.util.Objects;

/**
 * ReportClientInfoHandler
 */
public class ReportClientInfoHandler implements NettyRequestProcessor {

    private final AgentConfigurationProperties configurationProperties;

    private final Environment environment;

    @Autowired
    public ReportClientInfoHandler(AgentConfigurationProperties configurationProperties,
                                   Environment environment) {
        this.configurationProperties = configurationProperties;
        this.environment = environment;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null);
        String serializationProtocol = "http";
        String service = environment.getProperty("server.servlet.context-path", "");

        if (service.isEmpty()) service = configurationProperties.getService();
        if (service.isEmpty()) service = "未命名的服务";

        ClientInfo clientInfo = new ClientInfo(
                System.getProperty("os.name", "Windows 10"),
                Objects.requireNonNull(RemotingUtil.getHostname()),
                ApplicationType.REST_WEB,
                service,
                serializationProtocol);

        response.setBody(clientInfo.encode());
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
