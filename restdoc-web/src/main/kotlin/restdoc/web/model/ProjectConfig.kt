package restdoc.web.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "restdoc_project_config")
data class ProjectConfig(
        @Id var id: String,
        val projectId: String,
        val testURIPrefix: String
)
