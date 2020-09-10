package restdoc.web.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Serializable
@Document(collection = "restdoc_team")
data class Team(
        @Id val id: String,
        var name: String,
        var createTime: Long,
        var createBy: String,
        var owner: String,
        var cover: String
)
