package restdoc.web.controller.console.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import restdoc.web.base.auth.Verify
import restdoc.web.controller.console.model.UpdateWikiDto
import restdoc.web.core.Result
import restdoc.web.core.ok
import restdoc.web.model.SYS_ADMIN
import restdoc.web.repository.HttpDocumentRepository

@RequestMapping("/wiki")
@RestController
class WikiController {

    @Autowired
    private lateinit var httpDocumentRepository: HttpDocumentRepository

    @PatchMapping("")
    @Verify(role = [SYS_ADMIN])
    fun updateWiki(@RequestBody dto: UpdateWikiDto): Result {

        val updateResult = httpDocumentRepository.update(
                Query(Criteria("_id").`is`(dto.id)),
                Update().set("content", dto.content))

        return ok(dto.id)
    }
}