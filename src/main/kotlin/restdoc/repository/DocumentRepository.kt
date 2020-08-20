package restdoc.repository

import org.springframework.stereotype.Repository
import restdoc.base.mongo.BaseRepository
import restdoc.model.ApiDocument

@Repository
interface DocumentRepository : BaseRepository<ApiDocument, String> {
}