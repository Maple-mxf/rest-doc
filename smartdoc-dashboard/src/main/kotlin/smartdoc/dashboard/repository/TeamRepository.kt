package smartdoc.dashboard.repository

import org.springframework.stereotype.Repository
import smartdoc.dashboard.model.Team

@Repository
interface TeamRepository : smartdoc.dashboard.base.mongo.BaseRepository<Team, String>