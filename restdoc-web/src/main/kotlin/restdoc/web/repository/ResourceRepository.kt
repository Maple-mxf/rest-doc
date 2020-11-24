package restdoc.web.repository

import org.springframework.stereotype.Repository
import restdoc.web.model.Resource
import restdoc.web.base.mongo.BaseRepository

@Repository
interface ResourceRepository : BaseRepository<Resource, String>