package restdoc.web.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.HashIndexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.http.HttpMethod
import restdoc.web.util.FieldType
import restdoc.web.util.IDUtil.now
import kotlin.properties.Delegates

/**
 * DubboDocument
 */
@CompoundIndex(def = "{'javaClassName': 1, 'methodName': 1, 'paramTypes': 1}", unique = true)
@Document(collection = "restdoc_dubbo_document")
class DubboDocument {
    /**
     *
     */
    @Id lateinit var id: String

    /**
     *
     */
    lateinit var projectId: String

    /**
     * Resource Equals A Java Class
     */
    lateinit var resource: String

    /**
     *
     */
    lateinit var name: String

    /**
     * javaClassName restdoc.client.xxxx.XXXX
     */
    lateinit var javaClassName: String

    /**
     *
     */
    lateinit var methodName: String

    /**
     * Split By ','
     *
     * java.lang.Integer,java.util.List
     *
     */
    lateinit var paramTypes: String

    /**
     * Split By ','
     * name,age,var1,...
     */
    lateinit var paramNames: String

    /**
     *
     */
    lateinit var returnType: String

    /**
     * desc
     */
    lateinit var desc: String

    /**
     * Create time
     */
    var createTime by Delegates.notNull<Long>()

    /**
     *
     */
    lateinit var docType: DocType
}


enum class DocType {
    API,
    WIKI
}

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
        var requestHeaderDescriptor: List<HeaderFieldDescriptor>?,

        /**
         *
         */
        var requestBodyDescriptor: List<BodyFieldDescriptor>?,

        /**
         *
         */
        var responseBodyDescriptors: List<BodyFieldDescriptor>?,

        /**
         *
         */
        var queryParam: Map<String, Any>? = null,

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
        val executeResult: Map<String, Any?>? = null,

        /**
         *
         */
        val content: String? = null,

        /**
         *
         */
        var responseHeaderDescriptor: List<HeaderFieldDescriptor>? = null,

        /**
         *
         */
        val docType: DocType = DocType.API

)

enum class FieldDescType {
    HEADER, REQUEST_PARAM, RESPONSE_PARAM
}

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
        val createTime: Long = now(),

        /**
         * Field frequency
         */
        var frequency: Int = 1
)

/**
 * API test uri history
 */
@Document(collection = "restdoc_history_address")
data class HistoryAddress(@Id val id: String, val address: String,
                          @HashIndexed val documentId: String, val createTime: Long)


data class HeaderFieldDescriptor(
        val field: String,
        var value: List<String>,
        var description: String?,
        val optional: Boolean = false
)

data class BodyFieldDescriptor(
        var path: String,
        var value: Any?,
        var description: String?,
        val type: FieldType = FieldType.OBJECT,
        val optional: Boolean = false,
        val defaultValue: Any?
) {

    override fun equals(other: Any?): Boolean {
        if (other is BodyFieldDescriptor) {
            return this.path.equals(other.path)
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

data class URIVarDescriptor(
        val field: String,
        var value: Any,
        var description: String?
)

/**
 * Test case record
 */
data class TestCaseLog(val id: String)

