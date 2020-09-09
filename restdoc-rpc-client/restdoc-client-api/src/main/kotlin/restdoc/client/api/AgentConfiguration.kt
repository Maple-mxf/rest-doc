package restdoc.client.api

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(AgentConfigurationProperties::class)
open class AgentConfiguration {

    @Bean
    @ConditionalOnMissingBean
    open fun agentImpl(agentConfigurationProperties: AgentConfigurationProperties): AgentImpl =
            AgentImpl(agentConfigurationProperties)
}