package restdoc.repository

import org.springframework.stereotype.Repository
import restdoc.base.mongo.BaseRepository
import restdoc.model.Team

@Repository
interface TeamRepository : BaseRepository<Team, String> {
}