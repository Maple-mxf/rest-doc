package restdoc.web.model.doc.http

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.http.HttpMethod
import restdoc.web.model.doc.DocType

/**
 */
@Document(collection = "restdoc_restweb_document")
data class RestWebDocument(

        /**
         *
         */
        @Id var id: String?,

        /**
         *
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
         *
         */
        var requestHeaderDescriptor: List<HeaderFieldDescriptor>? = null,

        /**
         *
         */
        var requestBodyDescriptor: List<BodyFieldDescriptor>? = null,

        /**
         *
         */
        var responseBodyDescriptors: List<BodyFieldDescriptor>? = null,

        /**
         *
         */
        var queryParamDescriptors: List<QueryParamDescriptor>? = null,

        /**
         *
         */
        val method: HttpMethod = HttpMethod.GET,

        /**
         *
         */
        var uriVarDescriptors: List<URIVarDescriptor>?,

        /**
         *
         */
        @Deprecated(message = "content")
        val content: String? = null,

        /**
         *
         */
        var responseHeaderDescriptor: List<HeaderFieldDescriptor>? = null,

        /**
         *
         */
        val docType: DocType = DocType.API,

        /**
         *
         */
        var order: Int? = 0,

        /**
         *
         */
        var queryParamFieldDescriptor: List<QueryParamFieldDescriptor>? = null,

        /**
         * Doc的来源
         */
        var stem: Stem = Stem.CONSOLE
)