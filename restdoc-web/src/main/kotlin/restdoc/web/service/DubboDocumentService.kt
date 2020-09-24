package restdoc.web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import restdoc.remoting.common.DubboExposedAPI
import restdoc.web.controller.obj.ROOT_NAV
import restdoc.web.model.*
import restdoc.web.repository.DubboDocumentRepository
import restdoc.web.repository.ResourceRepository
import restdoc.web.util.IDUtil.now


/**
 * @author Overman
 */
interface DubboDocumentService {
    fun sync(projectId: String, apiList: List<DubboExposedAPI>)
}


@Service
open class DubboDocumentServiceImpl : DubboDocumentService {

    @Autowired
    lateinit var dubboDocumentRepository: DubboDocumentRepository

    @Autowired
    lateinit var resourceRepository: ResourceRepository

    override fun sync(projectId: String, apiList: List<DubboExposedAPI>) {

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

            for (method in api.exposedMethods) {
                // id = javaClass+method+paramType
                val id = "${api.refName}${method.methodName}${method.parameterClasses}".hashCode().toString()
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
                                    descriptor
                                }
                    }
                    
                    val descriptor = MethodReturnValueDescriptor()
                    descriptor.type = method.returnTypes[0]

                    this.projectId = projectId
                    this.docType = DocType.API
                    this.resource = resourceId
                }

                if (exist) {
                    // Update
                    val updateResult = dubboDocumentRepository.update(doc)

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