package restdoc.web.repository

import restdoc.web.model.ApplicationInstance
import restdoc.web.base.mongo.BaseRepository

interface ApplicationInstanceRepository : BaseRepository<ApplicationInstance, String> {
}