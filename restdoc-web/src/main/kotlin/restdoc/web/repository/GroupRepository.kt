package restdoc.web.repository

import org.springframework.stereotype.Repository
import restdoc.web.model.Group
import restdoc.web.base.mongo.BaseRepository

@Repository
interface GroupRepository : BaseRepository<Group, String> {
}