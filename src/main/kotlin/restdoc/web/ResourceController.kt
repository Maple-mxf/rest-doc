package restdoc.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.*
import restdoc.core.Result
import restdoc.core.ok
import restdoc.model.Resource
import restdoc.repository.ResourceRepository
import restdoc.util.IDUtil
import restdoc.util.IDUtil.now
import restdoc.web.obj.CreateResourceDto
import restdoc.web.obj.NavNode
import restdoc.web.obj.ROOT_NAV
import restdoc.web.obj.findChild
import javax.validation.Valid

@RestController
class ResourceController {

    @Autowired
    lateinit var resourceRepository: ResourceRepository

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
        return ok(mutableListOf(ROOT_NAV));
    }
}