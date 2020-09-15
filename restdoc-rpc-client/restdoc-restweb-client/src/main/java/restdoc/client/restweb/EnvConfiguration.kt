package restdoc.client.restweb

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.client.RestTemplate
import restdoc.client.restweb.context.EndpointsListener

/**
 * @author Overman
 * @since 2020/9/15
 */
@Configuration
open class EnvConfiguration {
    @Bean
    @ConditionalOnMissingBean
    open fun endpointsListener(environment: Environment) = EndpointsListener(environment)

    @Bean
    @ConditionalOnMissingBean
    open fun restWebInvokerImpl(environment: Environment, restTemplate: RestTemplate) = RestWebInvokerImpl(environment, restTemplate)

    @Bean
    @ConditionalOnMissingBean
    open fun restTemplate() = RestTemplate()
}