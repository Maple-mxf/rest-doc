package restdoc.client.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.client.RestTemplate
import restdoc.client.context.EndpointsListener
import restdoc.client.invoke.HttpInvoker
import restdoc.client.remoting.HttpApplicationAgent
import restdoc.client.remoting.HttpTaskRequestProcessor
import restdoc.client.remoting.PostEmptyApiTemplateRequestProcessor

@Configuration
@EnableConfigurationProperties(RestDocProperties::class)
open class RestDocClientConfiguration {

    @Bean @ConditionalOnMissingBean
    open fun applicationClient(restDocProperties: RestDocProperties,
                               httpTaskRequestProcessor: HttpTaskRequestProcessor,
                               postEmptyApiTemplateRequestProcessor: PostEmptyApiTemplateRequestProcessor
    ): HttpApplicationAgent {
        val httpApplicationAgent = HttpApplicationAgent(
                restDocProperties,
                httpTaskRequestProcessor,
                postEmptyApiTemplateRequestProcessor)
        httpApplicationAgent.connection()
        return httpApplicationAgent
    }

    @Bean @ConditionalOnMissingBean
    open fun httpTaskExecutor(restTemplate: RestTemplate, environment: Environment): HttpInvoker {
        return HttpInvoker(restTemplate, environment)
    }

    @Bean @ConditionalOnMissingBean
    open fun httpTaskRequestProcessor(httpInvoker: HttpInvoker?): HttpTaskRequestProcessor {
        return HttpTaskRequestProcessor(httpInvoker)
    }

    @Bean @ConditionalOnMissingBean
    open fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean @ConditionalOnMissingBean
    open fun endpointsListener(environment: Environment?): EndpointsListener {
        return EndpointsListener(environment)
    }

    @Bean @ConditionalOnMissingBean
    open fun postEmptyApiTemplateRequestProcessor(endpointsListener: EndpointsListener): PostEmptyApiTemplateRequestProcessor {
        return PostEmptyApiTemplateRequestProcessor(endpointsListener)
    }

}