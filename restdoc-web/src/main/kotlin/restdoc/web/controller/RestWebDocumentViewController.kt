package restdoc.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import restdoc.web.model.DocType
import restdoc.web.model.RestWebDocument
import restdoc.web.repository.ResourceRepository
import restdoc.web.repository.RestWebDocumentRepository

@Controller
class RestWebDocumentViewController {

    @Autowired
    lateinit var restWebDocumentRepository: RestWebDocumentRepository

    @Autowired
    lateinit var resourceRepository: ResourceRepository

    @Autowired
    lateinit var mapper: ObjectMapper

    // Router
    @GetMapping("/{projectId}/document/view/list/")
    fun list(@PathVariable projectId: String, model: Model): String = listView(projectId, model)

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
    fun createApi(@PathVariable projectId: String, model: Model): String {
        model.set("projectId", projectId)
        return "docs/add"
    }

    @GetMapping("/{projectId}/document/{documentId}/view")
    fun getApi(@PathVariable projectId: String, @PathVariable documentId: String, model: Model): String {
        model.set("documentId", documentId)

        val restWebDocument: RestWebDocument = restWebDocumentRepository.findById(documentId)
                .orElse(null)
                ?: return "docs/resourceDetail"

        val resource = resourceRepository.findById(restWebDocument.resource)

        model.addAttribute("resource", resource)
        model.addAttribute("document", restWebDocument)
        model.addAttribute("projectId", projectId)
        model.addAttribute("sample", mapper.writeValueAsString(restWebDocument.executeResult))

        return if (DocType.API == restWebDocument.docType) {
            "docs/apiDetail"
        } else if (DocType.WIKI == restWebDocument.docType) {
            "docs/wikiDetail"
        } else {
            "docs/resourceDetail"
        }
    }

    @GetMapping("/{projectId}/document/view/{id}/test")
    fun testApi(@PathVariable projectId: String, @PathVariable id: String, model: Model): String {

        val restWebDocument: RestWebDocument = restWebDocumentRepository.findById(id)
                .orElse(null)
                ?: return "view/error/500"

        model.addAttribute("initDocument", restWebDocument)
        model.addAttribute("projectId", projectId)

        return "docs/add"
    }
}