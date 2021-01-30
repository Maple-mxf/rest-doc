package smartdoc.dashboard.repository

import org.springframework.stereotype.Repository
import smartdoc.dashboard.model.Resource

@Repository
interface ResourceRepository : smartdoc.dashboard.base.mongo.BaseRepository<Resource, String>