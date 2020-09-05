package restdoc.client;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import restdoc.remoting.data.ApiEmptyTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 导入客户端所有的API 并且生成文档
 */
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

    private List<ApiEmptyTemplate> emptyApiTemplates;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = event.getApplicationContext()
                .getBean(RequestMappingHandlerMapping.class)
                .getHandlerMethods();

        // Must be flatmap handler method
        // because of The one handler method has many request pattern

        // final report the emptyApiTemplates to remoting server
        this.emptyApiTemplates = handlerMethods.entrySet()
                .stream()
                .flatMap(hm -> {
                    RequestMappingInfo requestMappingInfo = hm.getKey();
                    HandlerMethod handlerMethod = hm.getValue();

                    return requestMappingInfo.getPatternsCondition()
                            .getPatterns()
                            .stream()
                            .map(pattern -> {
                                ApiEmptyTemplate emptyTemplate = new ApiEmptyTemplate();
                                emptyTemplate.setSupportMethod(requestMappingInfo.getPatternsCondition().getPatterns()
                                        .toArray(new String[0]));
                                emptyTemplate.setFunction(handlerMethod.toString());
                                emptyTemplate.setPattern(pattern);

                                return emptyTemplate;
                            });
                }).collect(Collectors.toList());
    }

    public List<ApiEmptyTemplate> getEmptyApiTemplates() {
        return emptyApiTemplates;
    }
}
