package restdoc.web.model.doc.dubbo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import restdoc.web.model.doc.DocType
import restdoc.web.util.IDUtil

/**
 * DubboDocument
 *
 * @author Maple
 */
@CompoundIndex(def = "{'javaClassName': 1, 'methodName': 1, 'paramTypes': 1}", unique = true)
@Document(collection = "restdoc_dubbo_document")
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
    var createTime: Long = IDUtil.now()

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
