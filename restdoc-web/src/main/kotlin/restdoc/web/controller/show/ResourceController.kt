package restdoc.web.controller.show

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import restdoc.web.controller.show.obj.NavbarVO
import restdoc.web.repository.ResourceRepository
import restdoc.web.repository.RestWebDocumentRepository

//@RestController
class ResourceController {

    @Autowired
    lateinit var resourceRepository: ResourceRepository

    @Autowired
    lateinit var restWebDocumentRepository: RestWebDocumentRepository

    /**
     * <pre>
     *     [{
    "id": "ident",
    "text": "title text",
    "icon": "icon",
    "hasChildren": 0,
    "href": "targetUrl.php"
    }]
     */
    @GetMapping("/{projectId}/resource")
    fun resourceTree(@PathVariable projectId: String): Any {
        val resources = resourceRepository.list(Query(Criteria("projectId").`is`(projectId)))

        val docQuery = Query.query(Criteria("resource").`in`(resources.map { it.id }))
        val docs = restWebDocumentRepository.list(docQuery)

        val navNodes = resources.map {
            NavbarVO(id = it.id!!,
                    text = it.name!!,
                    hasChildren = if (docs.any { it.resource == it.id }) 1 else 0,
                    href = "")
        }

        return navNodes
    }

}