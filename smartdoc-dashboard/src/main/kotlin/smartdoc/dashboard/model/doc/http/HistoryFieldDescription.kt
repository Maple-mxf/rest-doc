package smartdoc.dashboard.model.doc.http

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.HashIndexed
import org.springframework.data.mongodb.core.mapping.Document
import smartdoc.dashboard.util.IDUtil

@Document(collection = "restdoc_history_field_description")
data class HistoryFieldDescription(
        @Id val id: String,

        /**
         * Field or Path
         *
         * Example: a.b.c:desc
         */
        @HashIndexed val field: String,

        /**
         * Must Not Empty
         */
        val description: String,

        /**
         *
         */
        val type: FieldDescType = FieldDescType.REQUEST_PARAM,

        /**
         * Project
         */
        val projectId: String,

        /**
         * Create Time
         */
        val createTime: Long = smartdoc.dashboard.util.IDUtil.now(),

        /**
         * Field frequency
         */
        var frequency: Int = 1
)