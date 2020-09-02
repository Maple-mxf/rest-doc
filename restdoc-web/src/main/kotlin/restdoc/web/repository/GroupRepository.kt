package restdoc.repository

import org.springframework.stereotype.Repository
import restdoc.base.mongo.BaseRepository
import restdoc.model.Group

@Repository
interface GroupRepository : BaseRepository<Group, String> {
}