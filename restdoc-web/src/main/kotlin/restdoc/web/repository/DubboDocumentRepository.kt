package restdoc.web.repository

import org.springframework.stereotype.Repository
import restdoc.web.base.mongo.BaseRepository
import restdoc.web.model.doc.dubbo.DubboDocument

@Repository
interface DubboDocumentRepository : BaseRepository<DubboDocument, String>