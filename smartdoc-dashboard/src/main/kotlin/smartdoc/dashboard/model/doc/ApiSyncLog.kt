package smartdoc.dashboard.model.doc

import org.springframework.data.mongodb.core.index.HashIndexed
import org.springframework.data.mongodb.core.mapping.Document
import smartdoc.dashboard.model.API_SYNC_LOG_COLLECTION

/**
 * @author Maple
 * @since 2.0.RELEASE
 */
@Document(collection = API_SYNC_LOG_COLLECTION)
class ApiSyncLog {

    @HashIndexed
    var projectId: String? = null

    @HashIndexed
    var documentId: String? = null

    var time: Long? = null

    var syncUserId: String? = null

    var application: String? = null

    var remote: String? = null
}