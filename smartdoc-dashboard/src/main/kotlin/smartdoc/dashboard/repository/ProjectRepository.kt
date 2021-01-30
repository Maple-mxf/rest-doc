package smartdoc.dashboard.repository

import org.springframework.stereotype.Repository
import smartdoc.dashboard.model.Project

@Repository
interface ProjectRepository : smartdoc.dashboard.base.mongo.BaseRepository<Project, String>