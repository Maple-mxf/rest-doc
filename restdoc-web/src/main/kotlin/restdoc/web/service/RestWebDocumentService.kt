package restdoc.web.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Objects
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import restdoc.rpc.client.common.model.http.RestWebApiDescriptor
import restdoc.web.model.Resource
import restdoc.web.model.doc.http.*
import restdoc.web.projector.JsonDeProjector
import restdoc.web.schedule.ScheduleController
import java.util.*

interface RestWebDocumentService {

    fun syncHttpApiDoc(clientId: String, projectId: String, user: String): Map<Resource, Map<Resource,List<RestWebDocument>>>
}

@Service
open class RestWebDocumentServiceImpl : RestWebDocumentService {

    @Autowired
    lateinit var scheduleController: ScheduleController

    @Autowired
    lateinit var mapper: ObjectMapper

    private fun mappingBody(rbps: Collection<RestWebApiDescriptor.ParameterDescriptor>) =
            rbps.flatMap { rbp ->
                if ("java.lang.Object" == rbp.type && rbp.supplementary != null && rbp.supplementary.toString().isNotBlank()) {
                    try {
                        val tree = mapper.readTree(rbp.supplementary.toString())
                        JsonDeProjector(tree).deProject()
                    } catch (e: Exception) {
                        e.printStackTrace(); listOf<BodyFieldDescriptor>()
                    }
                } else listOf()
            }

    private fun mappingHeader(rhps: Map<String, List<RestWebApiDescriptor.ParameterDescriptor>>) = rhps
            .map { rhp ->
                HeaderFieldDescriptor(field = rhp.key, value = listOf(), description = rhp.key)
            }


    override fun syncHttpApiDoc(clientId: String, projectId: String, user: String): Map<Resource, Map<Resource,List<RestWebDocument>>> {
        // Invoke remote client api info
        val emptyApiTemplates = scheduleController.syncGetEmptyApiTemplates(clientId)

        val ret = emptyApiTemplates
                .groupBy { it.packageName }
                .entries
                .map { it.key to it.value.groupBy { t -> t.controller } }
                .map {

                    val packageSource = Resource(
                            id = Objects.hashCode(it.first).toString(),
                            tag = it.first,
                            name = it.first,
                            pid = "root",
                            projectId = projectId,
                            createTime = Date().time,
                            order = 0,
                            createBy = user)

                    val classResourceMap = it.second.entries.map { t ->
                        val classResource = Resource(
                                id = Objects.hashCode(t.key).toString(),
                                tag = t.key,
                                name = t.key,
                                pid = packageSource.id,
                                projectId = projectId,
                                createTime = Date().time,
                                order = 0,
                                createBy = user)

                        val docs = t.value.map { d ->

                            val requestHeaderDescriptors = mappingHeader(d.requestHeaderParameters)
                            val responseHeaderDescriptors = mappingHeader(d.responseHeaderParameters)
                            val requestBodyDescriptors = mappingBody(d.requestBodyParameters)
                            val responseBodyDescriptors = mappingBody(listOf(d.responseBodyDescriptor))
                            val queryParamDescriptors = d.queryParamParameters.map { qpp -> QueryParamDescriptor(field = qpp.name, value = qpp.defaultValue) }
                            val uriVarsDescriptors = d.pathVariableParameters.map { pvp -> URIVarDescriptor(field = pvp.name, value = pvp.defaultValue) }
                            val matrixVarDescriptors = d.matrixVariableParameters.map { mvp ->
                                MatrixVariableDescriptor().apply {
                                    this.field = mvp.name
                                    this.defaultValue = mvp.defaultValue
                                    this.pathVar = mvp.supplementary.toString()
                                    this.required = mvp.require
                                }
                            }

                            RestWebDocument(
                                    id = Objects.hashCode(d.controller, d.pattern).toString(),
                                    projectId = projectId,
                                    name = d.name,
                                    resource = classResource.id!!,
                                    url = d.pattern,
                                    description = d.pattern,
                                    requestHeaderDescriptor = requestHeaderDescriptors,
                                    requestBodyDescriptor = requestBodyDescriptors,
                                    responseBodyDescriptors = responseBodyDescriptors,
                                    queryParamDescriptors = queryParamDescriptors,
                                    method = HttpMethod.resolve(d.method),
                                    uriVarDescriptors = uriVarsDescriptors,
                                    responseHeaderDescriptor = responseHeaderDescriptors,
                                    matrixVariableDescriptors = matrixVarDescriptors,
                                    stem = Stem.DEVELOPER_APPLICATION
                            )
                        }
                        classResource to docs
                    }.toMap()
                    packageSource to classResourceMap
                }.toMap()

        return ret
    }
}