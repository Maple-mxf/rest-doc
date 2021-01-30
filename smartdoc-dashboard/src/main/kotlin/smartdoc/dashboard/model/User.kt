package smartdoc.dashboard.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = USER_COLLECTION)
data class User(
        @Id val id: String,
        @Indexed(unique = true) var account: String,
        var password: String?,
        var createTime: Long?,
        var status: AccountStatus = AccountStatus.NORMAL,
        var teamId: String,
        var role: String = SYS_ADMIN
)