package restdoc.web.repository

import org.springframework.stereotype.Repository
import restdoc.web.base.mongo.BaseRepository
import restdoc.web.model.doc.http.RestWebDocument

@Repository
interface HttpDocumentRepository : BaseRepository<RestWebDocument, String>