package restdoc.web.controller.console

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.*
import restdoc.web.controller.console.obj.LayuiPageDto
import restdoc.web.controller.console.obj.layuiTableOK
import restdoc.web.repository.HttpApiTestLogRepository

@RestController
class HttpApiTestLogController {

    @Autowired
    private lateinit var httpApiTestLogRepository: HttpApiTestLogRepository

    @RequestMapping("/document/{documentId}/httpapitestlog/page")
    fun page(dto: LayuiPageDto, @PathVariable documentId: String): Any {
        val page = httpApiTestLogRepository.page(Query(Criteria("documentId").`is`(documentId)), dto.toPageable())
        return layuiTableOK(data = page.content, count = page.totalElements.toInt())
    }
}