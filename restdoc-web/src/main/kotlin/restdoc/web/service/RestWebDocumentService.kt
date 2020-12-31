package restdoc.web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import restdoc.web.model.Resource
import restdoc.web.model.doc.http.RestWebDocument
import restdoc.web.model.doc.http.Stem
import restdoc.web.model.doc.http.URIVarDescriptor
import restdoc.web.schedule.ScheduleController
import restdoc.web.util.IDUtil

interface RestWebDocumentService {

    fun syncHttpApiDoc(clientId: String, projectId: String): Map<Resource, List<RestWebDocument>>
}

@Service
open class RestWebDocumentServiceImpl : RestWebDocumentService {

    @Autowired
    lateinit var scheduleController: ScheduleController

    override fun syncHttpApiDoc(clientId: String, projectId: String): Map<Resource, List<RestWebDocument>> {
        // Invoke remote client api info
        val emptyApiTemplates = scheduleController.syncGetEmptyApiTemplates(clientId)

        val pid = "root"

        val map = emptyApiTemplates.groupBy { it.packageName }.entries
                .map { it.key to it.value.groupBy { t -> t.controller } }.toMap()


        return emptyApiTemplates
                // Group by resource
                .groupBy { it.controller }
                .map {

                    // Map key to resource
                    val resource = Resource(
                            id = IDUtil.id(),
                            tag = it.key,
                            name = it.key,
                            pid = pid,
                            projectId = projectId,
                            createTime = IDUtil.now(),
                            createBy = "Default"
                    )

                    // Map value to document
                    val documents = it.value.map { template ->

                        val uriVarDescriptors = template.pathVariableParameters
                                .map { pp ->
                                    URIVarDescriptor(pp.name, "", null)
                                }

                        RestWebDocument(id = IDUtil.id(),
                                projectId = projectId,
                                name = template.endpoint,
                                resource = resource.id!!,
                                url = template.pattern,
                                description = String.format("%s:%s", template.controller, template.endpoint),
                                requestHeaderDescriptor = mutableListOf(),
                                requestBodyDescriptor = mutableListOf(),
                                responseBodyDescriptors = mutableListOf(),
                                method = HttpMethod.valueOf(template.method),
                                uriVarDescriptors = uriVarDescriptors,
                                stem = Stem.DEVELOPER_APPLICATION)
                                .apply {

                                    // Set baseInfo
                                }
                    }
                    resource to documents
                }
                .toMap()
    }
}