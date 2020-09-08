package restdoc.client.dubbo

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DubboClientConfiguration {

    @Bean
    @ConditionalOnMissingBean
    open fun dubboApplicationAgent(beanFactory: ConfigurableListableBeanFactory): DubboApplicationAgent = DubboApplicationAgent(beanFactory)

    @Bean
    @ConditionalOnMissingBean
    open fun dubboContextHolder(beanFactory: ConfigurableListableBeanFactory) = DubboContextHolder(beanFactory)
}