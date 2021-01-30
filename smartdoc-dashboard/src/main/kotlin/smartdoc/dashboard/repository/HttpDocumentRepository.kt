package smartdoc.dashboard.repository

import org.springframework.stereotype.Repository
import smartdoc.dashboard.model.doc.http.HttpDocument

@Repository
interface HttpDocumentRepository : smartdoc.dashboard.base.mongo.BaseRepository<HttpDocument, String>