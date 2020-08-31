package restdoc.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.*
import restdoc.core.Result
import restdoc.core.ok
import restdoc.model.Resource
import restdoc.repository.DocumentRepository
import restdoc.repository.ResourceRepository
import restdoc.util.IDUtil
import restdoc.util.IDUtil.now
import restdoc.web.obj.*
import javax.validation.Valid

@RestController
class ResourceController {

    @Autowired
    lateinit var resourceRepository: ResourceRepository

    @Autowired
    lateinit var documentRepository: DocumentRepository

    @PostMapping("/{projectId}/resource")
    fun create(@PathVariable projectId: String, @Valid @RequestBody dto: CreateResourceDto): Result {
        val resource = Resource(
                id = IDUtil.id(),
                tag = dto.tag,
                name = dto.name,
                pid = dto.pid,
                projectId = projectId,
                createTime = now(),
                createBy = "System")
        resourceRepository.save(resource)
        return ok()
    }


    @GetMapping("/{projectId}/resource/tree")
    fun getTree(@PathVariable projectId: String): Result {
        val resources = resourceRepository.list(Query(Criteria("projectId").`is`(projectId)))

        val navNodes = resources.map {
            NavNode(id = it.id!!,
                    title = it.name!!,
                    field = "name",
                    children = null,
                    pid = it.pid!!)
        }
        findChild(ROOT_NAV, navNodes)

        val allNode = mutableListOf<NavNode>()
        allNode.add(ROOT_NAV)
        allNode.addAll(navNodes)

        val nodeIds = allNode.map { it.id }.toMutableList()

        val docs = documentRepository.list(Query(Criteria("resource").`in`(nodeIds)))

        for (navNode in allNode) {

            val childrenDocNode: MutableList<NavNode> = docs.filter { navNode.id.equals(it.resource) }
                    .map {
                        NavNode(
                                id = it.id!!,
                                title = it.name!!,
                                field = "",
                                children = mutableListOf(),
                                href = null,
                                pid = navNode.id,
                                spread = true,
                                checked = false,
                                type = NodeType.DOC
                        )
                    }.toMutableList()

            if (navNode.children != null) {
                navNode.children!!.addAll(childrenDocNode);
            } else {
                navNode.children = childrenDocNode
            }
        }

        return ok(mutableListOf(ROOT_NAV));
    }
}