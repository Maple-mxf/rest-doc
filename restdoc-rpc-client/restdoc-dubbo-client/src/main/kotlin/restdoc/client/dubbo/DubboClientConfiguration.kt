package restdoc.client.dubbo

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.CopyOnWriteArrayList

@Configuration
open class DubboClientConfiguration {

    /**
     * Init task in agent
     */
    @Bean
    @ConditionalOnMissingBean
    open fun dubboApplicationAgent(beanFactory: ConfigurableListableBeanFactory): DubboApplicationAgent {
        return DubboApplicationAgent(CopyOnWriteArrayList());
    }

    @Bean
    @ConditionalOnMissingBean
    open fun dubboContextHolder(beanFactory: ConfigurableListableBeanFactory) = DubboContextHolder(beanFactory)
}