package restdoc.web.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "restdoc_user")
data class User(
        @Id val id: String,
        var name: String?,
        var createTime: Long?,
        var status: AccountStatus = AccountStatus.NORMAL,
        var teamId: String
)