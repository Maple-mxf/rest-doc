package restdoc.client.restweb.handler;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import restdoc.client.restweb.AgentConfigurationProperties;
import restdoc.client.restweb.context.EndpointsListener;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSysResponseCode;
import restdoc.rpc.client.common.model.ApplicationType;
import restdoc.rpc.client.common.model.http.HttpApiPayload;

public class ExportApiHandler implements NettyRequestProcessor {

    private final AgentConfigurationProperties configurationProperties;

    private final EndpointsListener endpointsListener;

    private final Environment environment;

    @Autowired
    public ExportApiHandler(AgentConfigurationProperties configurationProperties,
                            EndpointsListener endpointsListener,
                            Environment environment) {
        this.configurationProperties = configurationProperties;
        this.endpointsListener = endpointsListener;
        this.environment = environment;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        HttpApiPayload apiBody = new HttpApiPayload();
        apiBody.setApiList(endpointsListener.getRestWebExposedAPIList());
        String service = environment.getProperty("server.servlet.context-path", configurationProperties.getService());
        if (service.isEmpty()) service = "未命名的服务";
        apiBody.setService(service);
        apiBody.setApplicationType(ApplicationType.REST_WEB);

        RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null);
        response.setBody(apiBody.encode());

        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
