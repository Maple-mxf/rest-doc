package restdoc.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Suppliers.ofInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import restdoc.core.Status
import restdoc.model.DocType
import restdoc.model.Document
import restdoc.repository.DocumentRepository

@Controller
class DocumentViewController {

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    lateinit var documentRepository: DocumentRepository

    @Autowired
    lateinit var mapper: ObjectMapper

    @GetMapping("/{projectId}/document/view/list/")
    fun list(@PathVariable projectId: String, model: Model): String {
        model.set("projectId", projectId)
        return "docs/list"
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

    @GetMapping("/document/{documentId}/view")
    fun getApi(@PathVariable documentId: String, model: Model): String {
        model.set("documentId", documentId)

        val document: Document = documentRepository.findById(documentId)
                .orElse(null)
                ?: return "docs/resourceDetail"

        model.addAttribute("document", document)
        model.addAttribute("sample", mapper.writeValueAsString(document.executeResult))

        if (DocType.API == document.docType) {
            return "docs/apiDetail"
        } else if (DocType.WIKI == document.docType) {
            return "docs/wikiDetail"
        } else {
            return "docs/resourceDetail"
        }
    }
}