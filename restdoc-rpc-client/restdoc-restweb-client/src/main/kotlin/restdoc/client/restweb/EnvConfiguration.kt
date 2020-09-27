package restdoc.client.restweb

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import restdoc.client.api.AgentConfigurationProperties
import restdoc.client.api.AgentImpl

@EnableConfigurationProperties(value = [AgentConfigurationProperties::class])
@Configuration
open class EnvConfiguration {

    @Bean(name = ["restWebAgentImpl"])
    open fun agentImpl(properties: AgentConfigurationProperties) = AgentImpl(properties)
}