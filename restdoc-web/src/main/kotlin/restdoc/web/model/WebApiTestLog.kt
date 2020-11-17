package restdoc.web.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.HashIndexed
import org.springframework.data.mongodb.core.mapping.Document


/**
 * WebApiTestLog
 *
 *
 * WebApiTestLog => {HeaderFieldDescriptor, BodyFieldDescriptor}
 */
@Document(collection = "restdoc_webapi_test_log")
class WebApiTestLog {

    @Id
    var id: String? = null

    var success: Boolean = true

    var remote: String? = null

    @HashIndexed
    var documentId: String? = null

    var url: String? = null

    var uriParameters: Map<String, Any>? = null

    var queryParameters: Map<String, Any>? = null

    var requestHeaderParameters: Map<String, List<String>>? = null

    var requestBodyParameters: Map<String, Any>? = null

    var responseStatus: Int = 200

    var responseHeader: Map<String, List<String>>? = null

    var responseBody: Any? = null

    var testTime: Long? = null
    
    var testMode: TestMode = TestMode.RPC
}