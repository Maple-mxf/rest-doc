package restdoc.web.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import restdoc.remoting.common.ApplicationType

@Document(collection = "restdoc_project")
data class Project(
        @Id
        val id: String,

        /**
         *
         */
        val name: String,

        /**
         *
         */
        val desc: String?,

        /**
         *
         */
        val createTime: Long?,

        /**
         *
         */
        val accessPwd: String? = null,

        /**
         *
         */
        val type: ApplicationType = ApplicationType.REST_WEB
)

