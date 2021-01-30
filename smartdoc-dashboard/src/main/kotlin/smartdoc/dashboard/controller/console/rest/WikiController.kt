package smartdoc.dashboard.controller.console.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import smartdoc.dashboard.controller.console.model.UpdateWikiDto
import smartdoc.dashboard.core.Result
import smartdoc.dashboard.core.ok
import smartdoc.dashboard.model.SYS_ADMIN
import smartdoc.dashboard.repository.HttpDocumentRepository


@RequestMapping("/wiki")
@RestController
class WikiController {

    @Autowired
    private lateinit var httpDocumentRepository: HttpDocumentRepository

    @PatchMapping("")
    @smartdoc.dashboard.base.auth.Verify(role = [SYS_ADMIN])
    fun updateWiki(@RequestBody dto: UpdateWikiDto): Result {

        val updateResult = httpDocumentRepository.update(
                Query(Criteria("_id").`is`(dto.id)),
                Update().set("content", dto.content))

        return ok(dto.id)
    }
}