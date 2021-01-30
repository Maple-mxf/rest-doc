package smartdoc.dashboard.controller.console.view

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import smartdoc.dashboard.controller.console.model.*
import smartdoc.dashboard.core.Status
import smartdoc.dashboard.model.doc.DocType
import smartdoc.dashboard.model.doc.http.HttpDocument
import smartdoc.dashboard.repository.HttpDocumentRepository
import smartdoc.dashboard.repository.ResourceRepository
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Controller
@smartdoc.dashboard.base.auth.Verify
class RestWebDocumentViewController {

    @Autowired
    lateinit var httpDocumentRepository: HttpDocumentRepository

    @Autowired
    lateinit var resourceRepository: ResourceRepository

    @Autowired
    lateinit var mapper: ObjectMapper

    @GetMapping("/{projectId}/document/view/list/")
    fun list(@PathVariable projectId: String, model: Model): String = listView(projectId, model)


    @GetMapping("/document/json/convert")
    fun jsonToDescriptor() = "docs/convert_json"

    // Handler Lambda Expression
    val listView: ((String, Model) -> String) =
            { projectId, model ->
                model.addAttribute("projectId", projectId)
                "docs/list"
            }

    @GetMapping("/{id}")
    fun get(): String {
        return ""
    }

    @GetMapping("/document/view/executeResult")
    fun executeResult(): String = "docs/executeResult"


    @GetMapping("/document/view/httpTask/{taskId}")
    fun execute(@PathVariable taskId: String, model: Model): String {
        model.addAttribute("taskId", taskId)
        return "docs/executeResult"
    }


    @GetMapping("/document/view/desc")
    fun desc(): String {
        return "docs/desc"
    }

    @GetMapping("/{projectId}/document/view/wiki/add")
    fun createWiki(@PathVariable projectId: String, model: Model): String {
        model.set("projectId", projectId)
        return "docs/addWiki"
    }

    @GetMapping("/{projectId}/document/view/api/add")
    @Deprecated(message = "createApi")
    fun createApi(@PathVariable projectId: String, model: Model): String {
        model.set("projectId", projectId)
        return "docs/web/api_test"
    }

    @GetMapping("/{projectId}/document/{documentId}/view")
    fun getApi(@PathVariable projectId: String, @PathVariable documentId: String, model: Model): String {
        model.set("documentId", documentId)

        val httpDocument: HttpDocument = httpDocumentRepository.findById(documentId)
                .orElse(null)
                ?: return "docs/resourceDetail"

        val resource = resourceRepository.findById(httpDocument.resource)

        model.addAttribute("resource", resource)
        model.addAttribute("projectId", projectId)
        // model.addAttribute("sample", mapper.writeValueAsString(restWebDocument.executeResult))

        return if (DocType.API == httpDocument.docType) {
            model.addAttribute("document", transformRestDocumentToVO(httpDocument))
            "docs/web/api_detail"
        } else if (DocType.WIKI == httpDocument.docType) {
            model.addAttribute("document", httpDocument)
            "docs/wiki_detail"
        } else {
            "docs/resourceDetail"
        }
    }

    @GetMapping("/{projectId}/document/view/{id}/test")
    fun testApi(@PathVariable projectId: String, @PathVariable id: String, model: Model): String {

        val httpDocument: HttpDocument = httpDocumentRepository.findById(id)
                .orElse(null)
                ?: return "view/error/500"

        model.addAttribute("initDocument", httpDocument)
        model.addAttribute("documentId", httpDocument.id)
        model.addAttribute("projectId", projectId)
        model.addAttribute("resource", httpDocument.resource)

        return "docs/web/api_test"
    }

    @Deprecated(message = "")
    @GetMapping("/document/{id}/description/view")
    fun editDescription(@PathVariable id: String,
                        @RequestParam type: String,
                        @RequestParam field: String,
                        model: Model): String {

        val httpDocument: HttpDocument = httpDocumentRepository.findById(id)
                .orElse(null)
                ?: return "view/error/500"

        val description: Any? = when (type) {
            "uri" -> {
                val uriField = httpDocument.uriVarDescriptors?.filter { it.field == field }?.first()
                uriField?.description
            }
            "requestBody" -> {
                val requestField = httpDocument.requestBodyDescriptor?.filter { it.path == field }?.first()
                requestField?.description
            }
            "responseBody" -> {
                val responseField = httpDocument.responseBodyDescriptors?.filter { it.path == field }?.first()
                responseField?.description
            }
            "requestHeader" -> {
                val requestHeader = httpDocument.requestHeaderDescriptor?.filter { it.field == field }?.first()
                requestHeader?.description
            }
            else -> {
                Status.INTERNAL_SERVER_ERROR.error()
            }
        }

        model.addAttribute("description", description ?: "")

        return "docs/edit_desc"
    }


    inner class PageView(val pageLocation: String, val field: Any?)

    @GetMapping("/document/{id}/snippet/view")
    fun editSnippetField(@PathVariable id: String,
                         @RequestParam type: String,
                         @RequestParam(name = "field") path: String,
                         model: Model): String {

        val field = URLDecoder.decode(path, StandardCharsets.UTF_8.name())

        val httpDocument: HttpDocument = httpDocumentRepository.findById(id)
                .orElse(null)
                ?: return "view/error/500"

        val pv = when (type) {
            "uri" -> {
                PageView("docs/edit_urivar", transformURIFieldToVO(
                        (httpDocument.uriVarDescriptors?.filter { it.field == field }) ?: mutableListOf())[0]
                )
            }
            "requestBody" -> {
                PageView("docs/edit_requestparam", transformNormalParamToVO(
                        (httpDocument.requestBodyDescriptor?.filter { it.path == field } ?: mutableListOf()))[0])
            }
            "responseBody" -> {
                PageView("docs/edit_responseparam",
                        transformNormalParamToVO((httpDocument.responseBodyDescriptors?.filter { it.path == field }
                                ?: mutableListOf()))[0]
                )
            }
            "requestHeader" -> {
                PageView("docs/edit_requestheader",
                        transformHeaderToVO(httpDocument.requestHeaderDescriptor?.filter { it.field == field }
                                ?: mutableListOf())[0])
            }
            "description" -> {
                PageView("docs/edit_description", httpDocument.description)
            }
            else -> {
                throw RuntimeException()
            }
        }
        model.addAttribute("field", pv.field)
        return pv.pageLocation
    }

    @GetMapping("/{projectId}/document/baseinfo/edit/view")
    fun baseInfo(@PathVariable projectId: String, model: Model): String {
        model.addAttribute("projectId", projectId)

        val resources = resourceRepository.list(Query().addCriteria(Criteria("projectId").`is`(projectId)))

        val navNodes = resources.map {
            NavNode(id = it.id!!,
                    title = it.name!!,
                    field = "name",
                    children = null,
                    pid = it.pid!!)
        }

        val rootNav: NavNode = NavNode(
                id = "root",
                title = "一级目录",
                field = "title",
                children = mutableListOf(),
                href = null,
                pid = "0",
                checked = true)

        findChild(rootNav, navNodes)

        val resourcePaths = mutableListOf<ResourcePath>()
        resourcePaths.add(ResourcePath(rootNav.title, rootNav.id))

        rootNav.children?.forEach {
            mapToResourcePath("一级目录", it, resourcePaths)
        }
        model.addAttribute("resourcePaths", resourcePaths)
        return "docs/edit_baseinfo"
    }

    /**
     * Split By '/'
     */
    private fun mapToResourcePath(path: String, node: NavNode, resourcePaths: MutableList<ResourcePath>) {
        if (node.children != null && node.children!!.isNotEmpty()) {
            node.children!!.forEach {
                mapToResourcePath("${path}/${node.title}", it, resourcePaths)
            }
        }
        resourcePaths.add(ResourcePath("${path}/${node.title}", node.id))
    }
}