package restdoc.web.controller.console.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.web.bind.annotation.*
import restdoc.web.controller.console.model.UpdateWikiDto
import restdoc.web.core.Result
import restdoc.web.core.ok
import restdoc.web.repository.RestWebDocumentRepository

@RequestMapping("/wiki")
@RestController
class WikiController {

    @Autowired
    private lateinit var restWebDocumentRepository: RestWebDocumentRepository

    @PatchMapping("")
    fun updateWiki(@RequestBody dto: UpdateWikiDto): Result {

        val updateResult = restWebDocumentRepository.update(
                Query(Criteria("_id").`is`(dto.id)),
                Update().set("content", dto.content))

        return ok(dto.id)
    }
}