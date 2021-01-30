package smartdoc.dashboard.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.HashIndexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @see
 */
@Document(collection = RESOURCE_COLLECTION)
data class Resource(

        /**
         *
         * Dubbo : javaClassName.hashCode
         *
         *
         */
        @Id
        var id: String?,

        /**
         *
         */
        var tag: String?,

        /**
         *
         */
        var name: String?,

        /**
         * 父ID
         */
        var pid: String?,

        /**
         *
         */
        @HashIndexed
        var projectId: String?,

        /**
         *
         */
        var createTime: Long?,

        /**
         *
         */
        var createBy: String?,

        /**
         *
         */
        var order: Int? = 0
)