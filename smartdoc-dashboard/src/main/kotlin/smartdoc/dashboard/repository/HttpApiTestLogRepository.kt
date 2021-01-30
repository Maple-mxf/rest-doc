package smartdoc.dashboard.repository

import org.springframework.stereotype.Repository
import smartdoc.dashboard.model.HttpApiTestLog

@Repository
interface HttpApiTestLogRepository : smartdoc.dashboard.base.mongo.BaseRepository<HttpApiTestLog, String>