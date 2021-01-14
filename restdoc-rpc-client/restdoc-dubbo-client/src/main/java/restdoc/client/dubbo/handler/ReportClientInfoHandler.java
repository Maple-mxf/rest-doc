package restdoc.client.dubbo.handler;

import io.netty.channel.ChannelHandlerContext;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import restdoc.client.api.model.ClientInfo;
import restdoc.client.dubbo.AgentConfigurationProperties;
import restdoc.remoting.common.RemotingUtil;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSysResponseCode;
import restdoc.rpc.client.common.model.ApplicationType;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The class ReportClientInfoHandler
 *
 * @author Maple
 */
@Component
public class ReportClientInfoHandler implements NettyRequestProcessor {

    private final ConfigurableListableBeanFactory beanFactory;

    private final AgentConfigurationProperties configurationProperties;

    @Autowired
    public ReportClientInfoHandler(ConfigurableListableBeanFactory beanFactory,
                                   AgentConfigurationProperties configurationProperties) {
        this.beanFactory = beanFactory;
        this.configurationProperties = configurationProperties;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null);
        String serializationProtocol = "dubbo";

        try {
            Map<String, ServiceBean> beansOfType = beanFactory.getBeansOfType(ServiceBean.class);
            if (!CollectionUtils.isEmpty(beansOfType)) {
                ProtocolConfig protocol = beansOfType.entrySet()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException())
                        .getValue()
                        .getProtocol();

                if (protocol != null) serializationProtocol = protocol.getName();
            }
        } catch (Throwable ignore) {
        }

        ClientInfo body = new ClientInfo(
                System.getProperty("os.name", "Windows 10"),
                RemotingUtil.getHostname(),
                ApplicationType.DUBBO,
                ConfigManager.getInstance().getApplication().map(ApplicationConfig::getName).orElse(configurationProperties.getService()),
                serializationProtocol);

        response.setBody(body.encode());
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
