package restdoc.client.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding


@ConstructorBinding
@ConfigurationProperties(prefix = "restdoc")
data class AgentConfigurationProperties(val host: String, val port: Int, val applicationName: String)