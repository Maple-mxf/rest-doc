package restdoc.repository

import org.springframework.stereotype.Repository
import restdoc.base.mongo.BaseRepository
import restdoc.model.Document

@Repository
interface DocumentRepository : BaseRepository<Document, String> {
}