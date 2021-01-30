package smartdoc.dashboard.repository

import org.springframework.stereotype.Repository
import smartdoc.dashboard.model.ApplicationInstance


@Repository
interface ApplicationInstanceRepository : smartdoc.dashboard.base.mongo.BaseRepository<ApplicationInstance, String>