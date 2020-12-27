package restdoc.client.dubbo;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class DubboRefBeanManager {

    private ConcurrentHashMap<String, Object> refBeanContainer = new ConcurrentHashMap<>();

    public void addRefBean(String beanName, Object bean) {
        refBeanContainer.putIfAbsent(beanName, bean);
    }

    public Object getRefBean(String beanName) {
        return refBeanContainer.get(beanName);
    }
}
