package smartdoc.dashboard.repository

import org.springframework.stereotype.Repository
import smartdoc.dashboard.model.doc.dubbo.DubboDocument

@Repository
interface DubboDocumentRepository : smartdoc.dashboard.base.mongo.BaseRepository<DubboDocument, String>