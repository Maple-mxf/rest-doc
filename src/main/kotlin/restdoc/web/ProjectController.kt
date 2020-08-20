package restdoc.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import restdoc.core.Result
import restdoc.core.ok

@RestController
@RequestMapping("/project")
class ProjectController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate
    
    fun list(): Result {
        return ok()
    }
}