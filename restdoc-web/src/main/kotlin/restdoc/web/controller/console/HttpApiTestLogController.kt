package restdoc.web.controller.console

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.*
import restdoc.web.base.auth.Verify
import restdoc.web.controller.console.model.BatchDeleteDto
import restdoc.web.controller.console.model.HttpApiTestLogDeProjectVO
import restdoc.web.controller.console.model.LayuiPageDto
import restdoc.web.controller.console.model.layuiTableOK
import restdoc.web.core.Result
import restdoc.web.core.Status
import restdoc.web.core.ok
import restdoc.web.repository.HttpApiTestLogRepository
import restdoc.web.util.dp.JsonDeProjector

@RestController
@Verify
class HttpApiTestLogController {

    @Autowired
    private lateinit var httpApiTestLogRepository: HttpApiTestLogRepository

    @Autowired
    private lateinit var mapper: ObjectMapper

    @RequestMapping("/document/{documentId}/httpapitestlog/page")
    fun page(dto: LayuiPageDto, @PathVariable documentId: String): Any {
        val query = Query(Criteria("documentId").`is`(documentId))
        query.with(Sort.by(Sort.Order.desc("createTime")))
        val page = httpApiTestLogRepository.page(query, dto.toPageable())
        return layuiTableOK(data = page.content, count = page.totalElements.toInt())
    }

    @PostMapping("/document/httpapitestlog/{id}/deProject")
    fun deProjectLogData(@PathVariable id: String): Result {

        val log = httpApiTestLogRepository.findById(id).orElseThrow { Status.BAD_REQUEST.instanceError() }

        val requestBodyParameters =
                if (log.requestBodyParameters != null)
                    JsonDeProjector(mapper.convertValue(log.requestBodyParameters, JsonNode::class.java)).deProject()
                else null

        val responseBodyParameters =
                if (log.responseBody != null)
                    JsonDeProjector(mapper.convertValue(log.responseBody, JsonNode::class.java)).deProject()
                else null

        val vo = HttpApiTestLogDeProjectVO(
                method = log.method!!,
                url = log.url!!,
                uriParameters = log.uriParameters,
                requestHeaderParameters = log.requestHeaderParameters,
                requestBodyParameters = requestBodyParameters,
                responseBodyParameters = responseBodyParameters)

        return ok(vo)
    }

    @DeleteMapping("/document/httpapitestlog/batch")
    fun batchDelete(@RequestBody dto: BatchDeleteDto): Result {
        val deleteResult = httpApiTestLogRepository.delete(Query(Criteria("id").`in`(dto.ids)))
        return ok()
    }

    @DeleteMapping("/document/httpapitestlog/{id}")
    fun delete(@PathVariable id: String): Result {
        httpApiTestLogRepository.deleteById(id)
        return ok()
    }
}