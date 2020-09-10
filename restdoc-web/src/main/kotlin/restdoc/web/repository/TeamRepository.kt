package restdoc.web.repository

import org.springframework.stereotype.Repository
import restdoc.web.model.Team
import restdoc.web.base.mongo.BaseRepository

@Repository
interface TeamRepository : BaseRepository<Team, String> {
}