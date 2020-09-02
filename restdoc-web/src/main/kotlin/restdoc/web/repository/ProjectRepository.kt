package restdoc.web.repository

import org.springframework.stereotype.Repository
import restdoc.web.model.Project
import restdoc.web.base.mongo.BaseRepository

@Repository
interface ProjectRepository : BaseRepository<Project, String> {
}