package restdoc.web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import restdoc.remoting.common.DubboExposedAPI
import restdoc.web.controller.obj.ROOT_NAV
import restdoc.web.model.*
import restdoc.web.repository.DubboDocumentRepository
import restdoc.web.repository.ResourceRepository
import restdoc.web.util.IDUtil.now
import restdoc.web.util.ReflectUtils


/**
 * @author Overman
 */
interface DubboDocumentService {
    fun sync(projectId: String, apiList: List<DubboExposedAPI>)
}

/**
 * DubboDocumentServiceImpl
 */
@Service
open class DubboDocumentServiceImpl : DubboDocumentService {

    @Autowired
    lateinit var dubboDocumentRepository: DubboDocumentRepository

    @Autowired
    lateinit var resourceRepository: ResourceRepository

    override fun sync(projectId: String, apiList: List<DubboExposedAPI>) {

        // 1 同步API
        for (api in apiList) {
            val resourceId = api.name.hashCode().toString()
            val resourceExist = resourceRepository.existsById(resourceId)

            if (!resourceExist) {
                val resource = Resource(
                        id = resourceId,
                        tag = api.name,
                        name = api.name,
                        pid = ROOT_NAV.id,
                        projectId = projectId,
                        createTime = now(),
                        createBy = "System")

                resourceRepository.save(resource)
            }

            //
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
                                        descriptor.primitive = ReflectUtils.isPrimitive(Class.forName(descriptor.type))
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

            // TODO  删除不存在的方法与接口

            val dubboOldExposedDocuments =
                    dubboDocumentRepository.list(Query().addCriteria(Criteria("resource").`is`(resourceId)))

            // 获取dubboOldExposedDocuments中不存在的文档数据
            val nonExistDocuments = dubboOldExposedDocuments.filter {
                api.exposedMethods.any { t ->
                    it.id != (api.name + t.methodName + t.parameterClasses.joinToString(separator = ",")).hashCode().toString()
                }
            }

            for (nonExistDocument in nonExistDocuments) {
                dubboDocumentRepository.deleteById(nonExistDocument.id)
            }
        }

        // 2 同步resource(删除不存在的resource)
        val apiIds = apiList.map { it.name.hashCode().toString() }
        val resources = resourceRepository.list(Query().addCriteria(Criteria("projectId").`is`(projectId)))
        val nonExistResources = resources.filter { apiIds.any { id -> id != it.id } }

        for (nonExistResource in nonExistResources) {
            resourceRepository.deleteById(nonExistResource.id)
        }
    }

}