package smartdoc.dashboard.repository

import org.springframework.stereotype.Repository
import smartdoc.dashboard.model.User

@Repository
interface UserRepository : smartdoc.dashboard.base.mongo.BaseRepository<User, String>