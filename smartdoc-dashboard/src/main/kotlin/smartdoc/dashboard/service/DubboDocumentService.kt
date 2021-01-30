package smartdoc.dashboard.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import restdoc.rpc.client.common.model.dubbo.DubboApiDescriptor
import smartdoc.dashboard.controller.console.model.NavNode
import smartdoc.dashboard.model.Resource
import smartdoc.dashboard.model.doc.DocType
import smartdoc.dashboard.model.doc.dubbo.DubboDocument
import smartdoc.dashboard.model.doc.dubbo.MethodParamDescriptor
import smartdoc.dashboard.model.doc.dubbo.MethodReturnValueDescriptor
import smartdoc.dashboard.repository.DubboDocumentRepository
import smartdoc.dashboard.repository.ResourceRepository
import smartdoc.dashboard.util.IDUtil.now
import smartdoc.dashboard.util.ReflectUtils


/**
 * @author Maple
 */
interface DubboDocumentService {
    fun sync(projectId: String, apiList: Collection<DubboApiDescriptor>)
}

/**
 * DubboDocumentServiceImpl
 *
 * @author Maple
 */
@Service
open class DubboDocumentServiceImpl : DubboDocumentService {

    @Autowired
    lateinit var dubboDocumentRepository: DubboDocumentRepository

    @Autowired
    lateinit var resourceRepository: ResourceRepository

    override fun sync(projectId: String, apiList: Collection<DubboApiDescriptor>) {

        // 0 同步resource(删除不存在的resource)
        val apiIds = apiList.map { it.name.hashCode().toString() }
        val resources = resourceRepository.list(Query().addCriteria(Criteria("projectId").`is`(projectId)))
        val nonExistResources = resources.filter { apiIds.any { id -> id != it.id } }
        for (nonExistResource in nonExistResources) {
            resourceRepository.deleteById(nonExistResource.id)
        }

        val rootNav: NavNode = NavNode(
                id = "root",
                title = "一级目录",
                field = "title",
                children = mutableListOf(),
                href = null,
                pid = "0",
                checked = true)

        // 1 同步API
        for (api in apiList) {
            val resourceId = api.name.hashCode().toString()
            val resourceExist = resourceRepository.existsById(resourceId)

            if (!resourceExist) {
                val simpleName = api.name.split('.').last()
                val resource = Resource(
                        id = resourceId,
                        tag = api.name,
                        name = simpleName,
                        pid = rootNav.id,
                        projectId = projectId,
                        createTime = now(),
                        createBy = "System")

                resourceRepository.save(resource)
            }

            // 0 删除不存在的文档
            val dubboOldExposedDocuments =
                    dubboDocumentRepository.list(Query().addCriteria(Criteria("resource").`is`(resourceId)))
            // 获取dubboOldExposedDocuments中不存在的文档数据
            dubboOldExposedDocuments.filter {
                api.exposedMethods.any { t ->
                    it.id != (api.name + t.methodName + t.parameterClasses.joinToString(separator = ",")).hashCode().toString()
                }
            }.forEach { dubboDocumentRepository.deleteById(it.id) }

            // 1 更新文档
            for (method in api.exposedMethods) {
                val id = (api.name + method.methodName + method.parameterClasses.joinToString(separator = ",")).hashCode().toString()
                val exist = dubboDocumentRepository.existsById(id)

                val doc = DubboDocument()

                doc.apply {
                    this.id = id
                    this.javaClassName = api.refName
                    this.methodName = method.methodName

                    if (method.parameterClasses != null && method.parameterNames != null) {
                        this.paramDescriptors = method.parameterNames.zip(method.parameterClasses)
                                .map {
                                    val descriptor = MethodParamDescriptor()
                                    descriptor.name = it.first
                                    descriptor.type = it.second

                                    try {
                                        descriptor.primitive = smartdoc.dashboard.util.ReflectUtils.isPrimitive(Class.forName(descriptor.type))
                                    } catch (ignored: Exception) {
                                        descriptor.primitive = false
                                    }
                                    descriptor
                                }
                    }

                    val descriptor = MethodReturnValueDescriptor()
                    descriptor.type = method.returnTypes[0]

                    this.returnValueDescriptor = descriptor

                    this.projectId = projectId
                    this.docType = DocType.API
                    this.resource = resourceId
                }

                if (exist) {
                    val updateResult = dubboDocumentRepository.update(doc)
                    println(updateResult)
                } else {
                    doc.apply {
                        this.name = method.methodName
                        this.createTime = now()
                    }
                    dubboDocumentRepository.save(doc)
                }
            }
        }
    }

}