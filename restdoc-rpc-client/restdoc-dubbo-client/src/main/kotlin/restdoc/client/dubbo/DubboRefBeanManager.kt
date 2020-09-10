package restdoc.client.dubbo

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
open class DubboRefBeanManager {

    private val refBeanContainer: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

    open fun addRefBean(beanName: String, bean: Any) {
        refBeanContainer.putIfAbsent(beanName, bean)
    }

    open fun getRefBean(beanName: String): Any {
        return refBeanContainer[beanName]!!
    }
}