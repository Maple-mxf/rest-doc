package restdoc.repository

import org.springframework.stereotype.Repository
import restdoc.base.mongo.BaseRepository
import restdoc.model.Resource

@Repository
interface ResourceRepository : BaseRepository<Resource, String> {
}