package restdoc.web.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Objects
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import restdoc.rpc.client.common.model.ApplicationType
import restdoc.rpc.client.common.model.http.HttpApiDescriptor
import restdoc.web.core.Status
import restdoc.web.model.RESOURCE_COLLECTION
import restdoc.web.model.Resource
import restdoc.web.model.doc.http.*
import restdoc.web.projector.JsonDeProjector
import restdoc.web.repository.ProjectRepository
import restdoc.web.schedule.ApiManager
import restdoc.web.util.MD5Util
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * WebDocumentService
 *
 * @author Maple
 */
interface HttpDocumentService {

    /**
     * @return {[Key(package)] -> ([Key(class)] => Docs)}
     */
    fun transformToHttpApiDoc(clientId: String, projectId: String, user: String): Map<Resource, Map<Resource, List<RestWebDocument>>>

    fun contrastHttpApiDoc(clientId: String, projectId: String, user: String)

    fun groupToTree(projectId: String, user: String, descriptors: List<HttpApiDescriptor>): Map<Resource, Map<Resource, List<RestWebDocument>>>
}

/**
 * RestWebDocumentServiceImpl
 *
 * @author Maple
 */
@Service
open class RestHttpDocumentServiceImpl(val mongoTemplate: MongoTemplate,
                                       val mapper: ObjectMapper,
                                       val apiManager: ApiManager,
                                       val projectRepository: ProjectRepository) : HttpDocumentService {

    override fun groupToTree(projectId: String, user: String, descriptors: List<HttpApiDescriptor>):
            Map<Resource, Map<Resource, List<RestWebDocument>>> {
        return descriptors
                .groupBy { it.packageName }
                .entries
                .map { it.key to it.value.groupBy { t -> t.controller } }
                .map {

                    val packageSource = Resource(
                            id = MD5Util.MD5Encode(it.first, StandardCharsets.UTF_8.name()),
                            tag = it.first,
                            name = it.first,
                            pid = "root",
                            projectId = projectId,
                            createTime = Date().time,
                            order = 0,
                            createBy = user)

                    val classResourceMap = it.second.entries.map { t ->
                        val classResource = Resource(
                                id = MD5Util.MD5Encode(t.key, StandardCharsets.UTF_8.name()),
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
    }

    private fun mappingBody(rbps: Collection<HttpApiDescriptor.ParameterDescriptor>) =
            rbps.flatMap { rbp ->
                if ("java.lang.Object" == rbp.type && rbp.supplementary != null) {
                    try {
                        return if (rbp.supplementary is String) {
                            val tree = mapper.readTree(rbp.supplementary.toString())
                            JsonDeProjector(tree).deProject()
                        } else {
                            val tree = mapper.readTree(mapper.writeValueAsString(rbp.supplementary))
                            JsonDeProjector(tree).deProject()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace(); listOf<BodyFieldDescriptor>()
                    }
                } else listOf()
            }

    private fun mappingHeader(rhps: Map<String, List<HttpApiDescriptor.ParameterDescriptor>>) =
            rhps.map { rhp -> HeaderFieldDescriptor(field = rhp.key, value = listOf(), description = rhp.key) }

    override fun transformToHttpApiDoc(clientId: String, projectId: String, user: String): Map<Resource, Map<Resource, List<RestWebDocument>>> {
        val project = projectRepository.findById(projectId)
                .orElseThrow { Status.INVALID_REQUEST.instanceError("invalid projectId") }

        val emptyApiTemplates = apiManager.list(clientId, ApplicationType.valueOf(project.type.name)) as List<HttpApiDescriptor>
        val ret = emptyApiTemplates
                .groupBy { it.packageName }
                .entries
                .map { it.key to it.value.groupBy { t -> t.controller } }
                .map {

                    // Package Resource
                    val packageSource = Resource(
                            id = MD5Util.MD5Encode(it.first, StandardCharsets.UTF_8.name()),
                            tag = it.first,
                            name = it.first,
                            pid = "root",
                            projectId = projectId,
                            createTime = Date().time,
                            order = 0,
                            createBy = user)

                    // Class Resource
                    val classResourceMap = it.second.entries.map { t ->
                        val classResource = Resource(
                                id = MD5Util.MD5Encode(t.key, StandardCharsets.UTF_8.name()),
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
                                    id = d.id(),
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


    /**
     * Contrast/Compared Api doc
     */
    override fun contrastHttpApiDoc(clientId: String,
                                    projectId: String,
                                    user: String) {
        val apiDocs = this.transformToHttpApiDoc(clientId, projectId, user)
        val rootSources = apiDocs.keys.toMutableSet()
        rootSources.addAll(apiDocs.values.flatMap { it.keys })

        // Query Mongo
        val resourceQuery = Query.query(Criteria("projectId").`is`(projectId))
        resourceQuery.fields().include("_id")
        val resourceIds = mongoTemplate.find(resourceQuery, Id::class.java, RESOURCE_COLLECTION).map { it._id }.toSet()

        //
        val newResources = rootSources.filter { !resourceIds.contains(it.id) }
        mongoTemplate.insertAll(newResources)


    }
}