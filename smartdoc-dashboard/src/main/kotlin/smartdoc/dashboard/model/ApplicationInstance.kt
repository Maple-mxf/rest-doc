package smartdoc.dashboard.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Application Instance info
 */
@Document(collection = "restdoc_application_instance")
data class ApplicationInstance(
        @Id
        val id: String?,
        val registryTime: Long,
        val hostName: String,
        val ip: String,
        val serviceName: String
)