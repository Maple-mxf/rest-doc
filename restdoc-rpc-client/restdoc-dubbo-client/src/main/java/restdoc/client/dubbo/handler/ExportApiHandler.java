package restdoc.client.dubbo.handler;

import io.netty.channel.ChannelHandlerContext;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import restdoc.client.api.AgentConfigurationProperties;
import restdoc.client.dubbo.DubboRefBeanManager;
import restdoc.client.dubbo.model.ServiceDescriptor;
import restdoc.remoting.netty.NettyRequestProcessor;
import restdoc.remoting.protocol.RemotingCommand;
import restdoc.remoting.protocol.RemotingSysResponseCode;
import restdoc.rpc.client.common.model.DubboExposedApiBody;
import restdoc.rpc.client.common.model.dubbo.DubboApiDescriptor;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The class ExportApiHandler
 *
 * @author Maple
 */
@Component
public class ExportApiHandler implements NettyRequestProcessor {

    private final ConfigurableListableBeanFactory beanFactory;
    private final DubboRefBeanManager refBeanManager;
    private final AgentConfigurationProperties configurationProperties;

    public ExportApiHandler(ConfigurableListableBeanFactory beanFactory,
                            DubboRefBeanManager refBeanManager,
                            AgentConfigurationProperties configurationProperties) {
        this.beanFactory = beanFactory;
        this.refBeanManager = refBeanManager;
        this.configurationProperties = configurationProperties;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SUCCESS, null);
        DubboExposedApiBody body = new DubboExposedApiBody();
        Map<String, ServiceBean> beansOfType = beanFactory.getBeansOfType(ServiceBean.class);

        List<DubboApiDescriptor> apiDescriptors = beansOfType.values()
                .stream()
                .map(serviceBean -> {
                    refBeanManager.addRefBean(serviceBean.getRef().getClass().getName(), serviceBean.getRef());

                    DubboApiDescriptor descriptor = new DubboApiDescriptor();
                    descriptor.setName(serviceBean.getBeanName());
                    descriptor.setRefName(serviceBean.getRef().getClass().getName());
                    ServiceDescriptor sd = new ServiceDescriptor(serviceBean.getInterfaceClass());

                    List<DubboApiDescriptor.ExposedMethod> exposedMethods = sd.getAllMethods()
                            .stream()
                            .map(mh -> new DubboApiDescriptor.ExposedMethod(
                                    mh.getParamDesc(),
                                    mh.getCompatibleParamSignatures(),
                                    Arrays.stream(mh.getParameterClasses()).map(Class::getName).toArray(String[]::new),
                                    mh.getParameterNames(),
                                    mh.getReturnClass().getName(),
                                    Arrays.stream(mh.getReturnTypes()).map(Type::getTypeName).toArray(String[]::new),
                                    mh.getMethodName(),
                                    mh.isGeneric()
                            )).collect(Collectors.toList());

                    descriptor.setExposedMethods(exposedMethods);

                    return descriptor;
                })
                .collect(Collectors.toList());

        body.setApiList(apiDescriptors);
        body.service = ConfigManager.getInstance().getApplication().map(ApplicationConfig::getName).orElse(configurationProperties.getService());
        response.setBody(body.encode());

        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
