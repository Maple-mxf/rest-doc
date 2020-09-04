package restdoc.client;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

/**
 * 导入客户端所有的API 并且生成文档
 */
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = event.getApplicationContext()
                .getBean(RequestMappingHandlerMapping.class)
                .getHandlerMethods();

        handlerMethods.forEach((info, method) -> {
            System.err.println(info.getPatternsCondition().getPatterns());
        });
    }
}
