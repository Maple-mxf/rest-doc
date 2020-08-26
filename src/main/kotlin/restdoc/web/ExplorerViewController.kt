package restdoc.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import restdoc.core.Status
import restdoc.repository.DocumentRepository

@Controller
@RequestMapping("/explorer/view")
class ExplorerViewController {

    @Autowired
    lateinit var documentRepository: DocumentRepository

    @Autowired
    lateinit var mapper: ObjectMapper

    @GetMapping("/{documentId}")
    fun get(@PathVariable documentId: String, model: Model): String {

        val document = documentRepository
                .findById(documentId)
                .orElseThrow { Status.INVALID_REQUEST.instanceError() }

        model.addAttribute("document", document)

        model.addAttribute("sample", mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(document.executeResult))

        return "explorer/detail"
    }

    @GetMapping("/{documentId}/test")
    fun command(@PathVariable documentId: String, model: Model): String {
        val document = documentRepository
                .findById(documentId)
                .orElseThrow { Status.INVALID_REQUEST.instanceError() }

        model.addAttribute("result", mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(document.executeResult))

        return "explorer/test"
    }
}