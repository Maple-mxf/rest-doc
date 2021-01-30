package smartdoc.dashboard.model.doc.dubbo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import smartdoc.dashboard.model.DUBBO_DOCUMENT_COLLECTION
import smartdoc.dashboard.model.doc.DocType

/**
 * DubboDocument
 *
 * @author Maple
 */
@CompoundIndex(def = "{'javaClassName': 1, 'methodName': 1, 'paramTypes': 1}", unique = true)
@Document(collection = DUBBO_DOCUMENT_COLLECTION)
class DubboDocument {
    /**
     *
     */
    @Id
    var id: String = ""

    /**
     *
     */
    var projectId: String = ""

    /**
     * Resource Equals A Java Class
     */
    var resource: String = ""

    /**
     *
     */
    var name: String = ""

    /**
     * javaClassName restdoc.client.xxxx.XXXX
     */
    var javaClassName: String = ""

    /**
     *
     */
    var methodName: String = ""

    /**
     * desc
     */
    var desc: String = ""

    /**
     * Create time
     */
    var createTime: Long = smartdoc.dashboard.util.IDUtil.now()

    /**
     *
     */
    var docType: DocType = DocType.API

    /**
     *
     */
    var paramDescriptors: List<MethodParamDescriptor> = listOf()

    /**
     *
     */
    var returnValueDescriptor: MethodReturnValueDescriptor = MethodReturnValueDescriptor()
}
