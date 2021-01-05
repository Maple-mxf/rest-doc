package restdoc.web.model.doc.http

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.http.HttpMethod
import restdoc.web.model.HTTP_DOCUMENT_COLLECTION
import restdoc.web.model.doc.DocType
import java.util.*

/**
 */
@Document(collection = HTTP_DOCUMENT_COLLECTION)
data class RestWebDocument(

        /**
         * id
         */
        @Id var id: String?,

        /**
         * projectId
         */
        var projectId: String?,

        /**
         *
         */
        var name: String?,

        /**
         *
         */
        var resource: String,

        /**
         * No ip or domain
         * and no port,net protocol
         *
         * This field example:/{contextPath}/...
         */
        val url: String,

        /**
         *
         */
        var description: String? = null,

        /**
         * requestHeaderDescriptor
         */
        var requestHeaderDescriptor: List<HeaderFieldDescriptor> = listOf(),

        /**
         * requestBodyDescriptor
         */
        var requestBodyDescriptor: List<BodyFieldDescriptor> = listOf(),

        /**
         * responseBodyDescriptors
         */
        var responseBodyDescriptors: List<BodyFieldDescriptor>? = null,

        /**
         * queryParamDescriptors
         */
        var queryParamDescriptors: List<QueryParamDescriptor>? = null,

        /**
         * method
         */
        val method: HttpMethod = HttpMethod.GET,

        /**
         * uriVarDescriptors
         */
        var uriVarDescriptors: List<URIVarDescriptor>?,

        /**
         * responseHeaderDescriptor
         */
        var responseHeaderDescriptor: List<HeaderFieldDescriptor>? = null,

        /**
         * matrixVariableDescriptors
         */
        var matrixVariableDescriptors: List<MatrixVariableDescriptor>? = null,

        /**
         *
         */
        val docType: DocType = DocType.API,

        /**
         *
         */
        var order: Int? = 0,

        /**
         * Wiki Content
         * If document is api doc. content is not used
         */
        var content: String? = null,

        /**
         * stem Doc的来源
         */
        var stem: Stem = Stem.CONSOLE,

        /**
         * Create Time
         */
        var createTime: Long = Date().time,

        /**
         *
         */
        var lastUpdateTime: Long = Date().time

)