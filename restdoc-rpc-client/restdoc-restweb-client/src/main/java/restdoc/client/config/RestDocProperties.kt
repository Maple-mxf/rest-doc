package restdoc.client.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "restdoc")
@ConstructorBinding
@Deprecated(message = "")
data class RestDocProperties(
        val serverIp: String,
        val serverPort: Int,
        val service: String? = null)