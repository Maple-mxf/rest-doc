package restdoc.web.controller.console.model

import org.springframework.http.HttpMethod
import restdoc.client.api.model.Invocation
import restdoc.web.base.getBean
import restdoc.web.core.code.CURLCodeSampleGenerator
import restdoc.web.core.code.JavaCodeSampleGenerator
import restdoc.web.core.code.PythonCodeSampleGenerator
import restdoc.web.model.doc.http.*


fun findChild(parentNode: NavNode, navNodes: List<NavNode>) {
    val children: MutableList<NavNode> = navNodes.filter { it.pid == parentNode.id }.toMutableList()
    parentNode.children = children
    for (child in children) {
        findChild(child, navNodes)
    }
}

enum class NodeType {
    RESOURCE, WIKI, API
}

data class NavNode(var id: String,
                   var title: String,
                   var field: String?,
                   var children: MutableList<NavNode>?,
                   var href: String? = null,
                   var pid: String,
                   var spread: Boolean = true,
                   var checked: Boolean = false,
                   var disabled: Boolean = false,
                   var type: NodeType = NodeType.RESOURCE

)


/**
 * Layui data format
 *
 * {
"code": 0,
"msg": "",
"count": 1000,
"data": [{}, {}]
}
 *
 */
fun layuiTableOK(data: Any, count: Int): LayuiTable = LayuiTable(code = 0, count = count, data = data, msg = null)

data class LayuiTable(val code: Int, var msg: String?, val count: Int = 0, val data: Any? = null)

data class HeaderFieldDescriptorVO(val field: String, val value: String, val optional: String = "是", val description: String = "")

data class BodyFieldDescriptorVO(val path: String, val value: Any = "",
                                 val optional: String = "是", val description: String = "",
                                 val type: String = "Object")

data class URIVarDescriptorVO(val field: String, val value: String, val description: String)


data class RestWebDocumentVO(

        val id: String,


        val method: String,

        /**
         *
         */
        val projectId: String,


        /**
         *
         */
        val name: String,


        /**
         *
         */
        val resource: String,

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
        val description: String = "",

        /**
         *
         */
        val requestHeaderDescriptor: List<HeaderFieldDescriptorVO> = listOf(),

        /**
         *
         */
        val requestBodyDescriptor: List<BodyFieldDescriptorVO> = listOf(),

        /**
         *
         */
        val responseBodyDescriptors: List<BodyFieldDescriptorVO> = listOf(),

        /**
         *
         */
        val queryParamDescriptors: List<QueryParamDescriptor> = listOf(),


        val uriVarDescriptors: List<URIVarDescriptorVO> = listOf(),

        /**
         *
         */
        val responseHeaderDescriptors: List<HeaderFieldDescriptorVO> = listOf(),

        /**
         * CURL Code sample
         */
        val curlCodeSample: String = "",

        /**
         * Java Code sample
         */
        val javaCodeSample: String = "",

        /**
         * Python code sample
         */
        val pythonCodeSample: String = ""
)

fun transformHeaderToVO(headers: List<HeaderFieldDescriptor>) =
        headers.map {
            HeaderFieldDescriptorVO(field = it.field, value = it.value.joinToString(separator = ","), description = it.description
                    ?: "", optional = if (it.optional) "是" else "否")
        }.toMutableList()

fun transformNormalParamToVO(params: List<BodyFieldDescriptor>) =
        params.map {
            BodyFieldDescriptorVO(path = it.path, value = it.value
                    ?: "", optional = if (it.optional) "是" else "否", type = it.type.name.toLowerCase(), description = it.description
                    ?: "")
        }.toMutableList()


fun transformURIFieldToVO(uriVars: List<URIVarDescriptor>) =
        uriVars.map {
            URIVarDescriptorVO(
                    field = it.field,
                    value = it.value.toString(),
                    description = it.description ?: ""
            )
        }.toMutableList()


fun transformRestDocumentToVO(doc: RestWebDocument) = RestWebDocumentVO(
        id = doc.id!!,
        method = doc.method.name,
        projectId = doc.projectId!!,
        name = doc.name ?: "",
        resource = doc.resource,
        url = doc.url,
        description = if (doc.description == null || doc.description!!.isBlank()) "API说明" else doc.description!!,
        requestHeaderDescriptor = transformHeaderToVO(doc.requestHeaderDescriptor ?: listOf()),
        responseBodyDescriptors = transformNormalParamToVO(doc.responseBodyDescriptors ?: listOf()),
        requestBodyDescriptor = transformNormalParamToVO(doc.requestBodyDescriptor ?: listOf()),
        uriVarDescriptors = transformURIFieldToVO(doc.uriVarDescriptors ?: listOf()),
        responseHeaderDescriptors = transformHeaderToVO(doc.responseHeaderDescriptor ?: listOf()),
        queryParamDescriptors = if (doc.queryParamDescriptors != null) doc.queryParamDescriptors!! else listOf(),
        curlCodeSample = getBean(CURLCodeSampleGenerator::class.java).invoke(doc),
        javaCodeSample = getBean(JavaCodeSampleGenerator::class.java).invoke(doc),
        pythonCodeSample = getBean(PythonCodeSampleGenerator::class.java).invoke(doc)
)

data class ResourcePath(val path: String, val id: String)

data class TestDubboMicroserviceResult(
        val method: String,
        val paramTypes: String,
        val success: Boolean,
        val errorMessage: String? = "",
        val returnValue: Any? = "",
        val returnType: String = "void",
        val time: Long = 0L
)

data class SyncDocumentResultVo(val totalQuantity: Int, val savedQuantity: Int)

data class DTreeVO(val id: String,
                   val title: String,
                   val parentId: String,
                   var children: List<Any> = listOf(),
                   var type: NodeType = NodeType.RESOURCE,
                   var iconClass: String? = null,
                   val spread: Boolean = false)

data class HttpApiTestLogDeProjectVO(
        val method: HttpMethod,
        val url: String,
        val uriParameters: Map<String, Any?>?,
        val requestHeaderParameters: Map<String, Any?>?,
        val requestBodyParameters: Map<String, Any?>?,
        val responseBodyParameters: Any?,
        val responseHeaderParameters: Map<String, Any?>? = null
)

data class SearchHeaderKeyVO(
        val headerKey: String
)

data class SearchHeaderValueVO(
        val headerValue: String
)

data class QueryParamKeyValueVO(val key: String, val value: Any?)

data class RestWebInvocationResultVO(val isSuccessful: Boolean,
                                     val exceptionMsg: String?,
                                     val invocation: Invocation,
                                     val status: Int,
                                     val responseHeaders: MutableMap<String, MutableList<String>>,
                                     val responseBody: Any?,
                                     val queryParam: Map<String, Any?>? = null)