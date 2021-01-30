package smartdoc.dashboard.model.doc.http

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*


/**
 * The class HttpDocRestrictionRule
 *
 * @see org.springframework.web.bind.annotation.RequestMapping
 */
@Document(collection = "restdoc_httpdoc_restriction_rule")
class HttpDocRestrictionRule {

    var id: String? = null

    var createTime: Long = Date().time

    var requestHeaders: List<HttpDocRestrictionRuleDescriptor>? = null

    var consumes: List<HttpDocRestrictionRuleDescriptor>? = null

    var produces: List<HttpDocRestrictionRuleDescriptor>? = null

    var params: List<HttpDocRestrictionRuleDescriptor>? = null

    @Indexed(unique = true)
    var documentId: String? = null
}