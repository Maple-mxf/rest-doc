package restdoc.web.repository

import org.springframework.stereotype.Repository
import restdoc.web.base.mongo.BaseRepository
import restdoc.web.model.ApplicationInstance


@Repository
interface ApplicationInstanceRepository : BaseRepository<ApplicationInstance, String>