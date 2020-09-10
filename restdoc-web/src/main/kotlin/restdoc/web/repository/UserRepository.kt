package restdoc.web.repository

import org.springframework.stereotype.Repository
import restdoc.web.model.User
import restdoc.web.base.mongo.BaseRepository

@Repository
interface UserRepository : BaseRepository<User, String> {
}