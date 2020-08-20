package restdoc.repository

import org.springframework.stereotype.Repository
import restdoc.base.mongo.BaseRepository
import restdoc.model.Project

@Repository
interface ProjectRepository : BaseRepository<Project, String> {
}