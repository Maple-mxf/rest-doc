package restdoc.client.dubbo

import org.apache.dubbo.config.spring.ServiceBean
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.CommandLineRunner
import java.util.concurrent.ConcurrentHashMap

class DubboContextHolder(private val beanFactory: ConfigurableListableBeanFactory) : CommandLineRunner {

    val exportInterfaces: MutableMap<String, ServiceBean<*>> = ConcurrentHashMap()

    override fun run(vararg args: String?) {
        val beansOfType = beanFactory.getBeansOfType(ServiceBean::class.java)
        if (exportInterfaces.isEmpty()) exportInterfaces.putAll(beansOfType.toMap())
    }

}