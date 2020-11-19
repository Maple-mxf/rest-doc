package restdoc.web.repository

import org.springframework.stereotype.Repository
import restdoc.web.base.mongo.BaseRepository
import restdoc.web.model.HttpApiTestLog

@Repository
interface HttpApiTestLogRepository : BaseRepository<HttpApiTestLog, String> {
}