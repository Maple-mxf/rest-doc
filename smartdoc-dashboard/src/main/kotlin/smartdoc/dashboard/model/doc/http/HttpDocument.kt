package smartdoc.dashboard.model.doc.http

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.http.HttpMethod
import smartdoc.dashboard.model.HTTP_DOCUMENT_COLLECTION
import smartdoc.dashboard.model.doc.DocType
import java.util.*

/**
 */
@Document(collection = HTTP_DOCUMENT_COLLECTION)
data class HttpDocument(

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
        var requestHeaderDescriptor: MutableList<HeaderFieldDescriptor> = mutableListOf(),

        /**
         * requestBodyDescriptor
         */
        var requestBodyDescriptor: MutableList<BodyFieldDescriptor> = mutableListOf(),

        /**
         * responseBodyDescriptors
         */
        var responseBodyDescriptors: MutableList<BodyFieldDescriptor> =  mutableListOf(),

        /**
         * queryParamDescriptors
         */
        var queryParamDescriptors: MutableList<QueryParamDescriptor> =  mutableListOf(),

        /**
         * method
         */
        val method: HttpMethod = HttpMethod.GET,

        /**
         * uriVarDescriptors
         */
        var uriVarDescriptors: MutableList<URIVarDescriptor> = mutableListOf(),

        /**
         * responseHeaderDescriptor
         */
        var responseHeaderDescriptor: MutableList<HeaderFieldDescriptor> =  mutableListOf(),

        /**
         * matrixVariableDescriptors
         */
        var matrixVariableDescriptors: MutableList<MatrixVariableDescriptor> =  mutableListOf(),

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