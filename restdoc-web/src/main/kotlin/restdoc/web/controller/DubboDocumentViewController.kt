package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import restdoc.web.core.Status
import restdoc.web.repository.DubboDocumentRepository
import restdoc.web.repository.ResourceRepository

/**
 */
@Controller
class DubboDocumentViewController {

    @Autowired
    lateinit var dubboDocumentRepository: DubboDocumentRepository

    @Autowired
    lateinit var resourceRepository: ResourceRepository

    @GetMapping("/{resourceId}/dubboDocument")
    fun getDocsByResources(@PathVariable resourceId: String, model: Model): String {

        val resource = resourceRepository.findById(resourceId).orElseThrow(Status.BAD_REQUEST::instanceError)

        val query = Query().addCriteria(Criteria("resource").`is`(resourceId))
        val docs = dubboDocumentRepository.list(query)

        docs.forEach {
            if (it.desc.isBlank()) it.desc = "此处填写备注"
        }

        model.addAttribute("docs", docs)

        model.addAttribute("resourceName", resource.name)

        return "docs/dubbo_doc_detail"
    }

}