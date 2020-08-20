package restdoc.repository

import org.springframework.stereotype.Repository
import restdoc.base.mongo.BaseRepository
import restdoc.model.User

@Repository
interface UserRepository : BaseRepository<User, String> {
}