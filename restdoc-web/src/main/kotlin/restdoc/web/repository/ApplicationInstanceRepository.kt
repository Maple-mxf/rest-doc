package restdoc.repository

import restdoc.base.mongo.BaseRepository
import restdoc.model.ApplicationInstance

interface ApplicationInstanceRepository : BaseRepository<ApplicationInstance, String> {
}