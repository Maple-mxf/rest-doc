package restdoc.web.repository

import org.springframework.stereotype.Repository
import restdoc.web.model.Document
import restdoc.web.base.mongo.BaseRepository

@Repository
interface DocumentRepository : BaseRepository<Document, String> {
}