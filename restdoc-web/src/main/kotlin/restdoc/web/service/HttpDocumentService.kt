package restdoc.web.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpMethod
import org.springframework.lang.NonNull
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ValueConstants
import restdoc.rpc.client.common.model.ApplicationType
import restdoc.rpc.client.common.model.http.HttpApiDescriptor
import restdoc.web.core.Status
import restdoc.web.distributelock.LockKey
import restdoc.web.model.Resource
import restdoc.web.model.doc.http.*
import restdoc.web.projector.JsonDeProjector
import restdoc.web.repository.HttpDocumentRepository
import restdoc.web.repository.ProjectRepository
import restdoc.web.repository.ResourceRepository
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
    fun transformToHttpApiDoc(clientId: String, projectId: String, user: String): Map<Resource, Map<Resource, List<HttpDocument>>>

    fun importApi(clientId: String, projectId: String, user: String, selectedApiIds: List<String>)
}

/**
 * RestWebDocumentServiceImpl
 *
 * @author Maple
 */
@Service
open class HttpDocumentServiceImpl(val mongoTemplate: MongoTemplate,
                                   val mapper: ObjectMapper,
                                   val apiManager: ApiManager,
                                   val projectRepository: ProjectRepository,
                                   val resourceRepository: ResourceRepository,
                                   val httpDocumentRepository: HttpDocumentRepository) : HttpDocumentService {

    private fun bodyProject(rbps: Collection<HttpApiDescriptor.ParameterDescriptor>) =
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

    // TODO
    private fun headerProject(rhps: Map<String, List<HttpApiDescriptor.ParameterDescriptor>>) =
            rhps.map { rhp ->
                // ALS MediaType
                //
                val values = rhp.value
                        .map {
                            if (ValueConstants.DEFAULT_NONE == it.defaultValue) {
                                "${it.name}={${it.name}}"
                            } else {
                                "${it.name}=${it.defaultValue}"
                            }
                        }
                HeaderFieldDescriptor(field = rhp.key, value = values, description = rhp.key)
            }

    override fun transformToHttpApiDoc(clientId: String,
                                       projectId: String,
                                       user: String): Map<Resource, Map<Resource, List<HttpDocument>>> {

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

                            val requestHeaderDescriptors = headerProject(d.requestHeaderParameters)
                            val responseHeaderDescriptors = headerProject(d.responseHeaderParameters)
                            val requestBodyDescriptors = bodyProject(d.requestBodyParameters)
                            val responseBodyDescriptors = bodyProject(listOf(d.responseBodyDescriptor))
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

                            HttpDocument(
                                    id = d.id(),
                                    projectId = projectId,
                                    name = d.name,
                                    resource = classResource.id!!,
                                    url = d.pattern,
                                    description = d.pattern,
                                    requestHeaderDescriptor = requestHeaderDescriptors.toMutableList(),
                                    requestBodyDescriptor = requestBodyDescriptors.toMutableList(),
                                    responseBodyDescriptors = responseBodyDescriptors.toMutableList(),
                                    queryParamDescriptors = queryParamDescriptors.toMutableList(),
                                    method = HttpMethod.resolve(d.method),
                                    uriVarDescriptors = uriVarsDescriptors.toMutableList(),
                                    responseHeaderDescriptor = responseHeaderDescriptors.toMutableList(),
                                    matrixVariableDescriptors = matrixVarDescriptors.toMutableList(),
                                    stem = Stem.DEVELOPER_APPLICATION
                            )
                        }
                        classResource to docs
                    }.toMap()
                    packageSource to classResourceMap
                }.toMap()

        return ret
    }


    // @DistributeLock(name = "importHttpApi", type = DistributeLockType.REDIS)
    override fun importApi(clientId: String, @NonNull @LockKey projectId: String, user: String, selectedApiIds: List<String>) {

        val table = transformToHttpApiDoc(clientId, projectId, user)

        val savedPkResourceIds = mutableListOf<String>()
        val savedClassResourceIds = mutableListOf<String>()

        for (pkEntry in table) {
            val pkResource = pkEntry.key

            var hasDoc = false

            for (classEntry in pkEntry.value) {
                val classResource = classEntry.key
                val docs = classEntry.value
                val matchedDocs = docs.filter { selectedApiIds.contains(it.id) }

                if (matchedDocs.isNotEmpty()) {
                    hasDoc = true

                    if (!resourceRepository.existsById(classResource.id)) {
                        resourceRepository.save(classResource)
                        savedClassResourceIds.add(classResource.id!!)
                    }

                    for (document in matchedDocs) {

                        val docExist = httpDocumentRepository.existsById(document.id)

                        if (docExist) {
                            val oldDocument = httpDocumentRepository.findById(document.id).orElse(null)

                            if (oldDocument == null) {
                                httpDocumentRepository.save(document)
                            } else {
                                if (document.requestHeaderDescriptor.isNotEmpty()) {
                                    if (oldDocument.requestHeaderDescriptor.isEmpty())
                                        oldDocument.requestHeaderDescriptor = document.requestHeaderDescriptor
                                    else {
                                        val fieldMap =
                                                oldDocument.requestHeaderDescriptor.map { it.field to it }
                                                        .toMap()

                                        document.requestHeaderDescriptor
                                                .forEach { h ->
                                                    if (!fieldMap.keys.contains(h.field))
                                                        oldDocument.requestHeaderDescriptor.add(h)
                                                }
                                    }
                                }

                                if (document.requestBodyDescriptor.isNotEmpty()) {
                                    if (oldDocument.requestBodyDescriptor.isEmpty())
                                        oldDocument.requestBodyDescriptor = document.requestBodyDescriptor
                                    else {
                                        val fieldMap =
                                                oldDocument.requestBodyDescriptor.map { it.path to it }
                                                        .toMap()

                                        document.requestBodyDescriptor
                                                .forEach { h ->
                                                    if (!fieldMap.keys.contains(h.path))
                                                        oldDocument.requestBodyDescriptor.add(h)
                                                }
                                    }
                                }

                                if (document.uriVarDescriptors.isNotEmpty()) {

                                    if (oldDocument.uriVarDescriptors.isEmpty())
                                        oldDocument.uriVarDescriptors = document.uriVarDescriptors
                                    else {
                                        val fieldMap =
                                                oldDocument.uriVarDescriptors.map { it.field to it }
                                                        .toMap()

                                        document.uriVarDescriptors
                                                .forEach { h ->
                                                    if (!fieldMap.keys.contains(h.field))
                                                        oldDocument.uriVarDescriptors.add(h)
                                                }
                                    }

                                }

                                if (document.matrixVariableDescriptors.isNotEmpty()) {

                                    if (oldDocument.matrixVariableDescriptors.isEmpty())
                                        oldDocument.matrixVariableDescriptors = document.matrixVariableDescriptors
                                    else {
                                        val fieldMap =
                                                oldDocument.matrixVariableDescriptors.map { it.field to it }
                                                        .toMap()

                                        document.matrixVariableDescriptors
                                                .forEach { h ->
                                                    if (!fieldMap.keys.contains(h.field))
                                                        oldDocument.matrixVariableDescriptors.add(h)
                                                }
                                    }
                                }

                                if (document.queryParamDescriptors.isNotEmpty()) {
                                    if (oldDocument.queryParamDescriptors.isEmpty())
                                        oldDocument.queryParamDescriptors = document.queryParamDescriptors
                                    else {
                                        val fieldMap =
                                                oldDocument.queryParamDescriptors.map { it.field to it }
                                                        .toMap()

                                        document.queryParamDescriptors
                                                .forEach { h ->
                                                    if (!fieldMap.keys.contains(h.field))
                                                        oldDocument.queryParamDescriptors.add(h)
                                                }
                                    }
                                }

                                if (document.responseHeaderDescriptor.isNotEmpty()) {
                                    if (oldDocument.responseHeaderDescriptor.isEmpty())
                                        oldDocument.responseHeaderDescriptor = document.responseHeaderDescriptor
                                    else {
                                        val fieldMap =
                                                oldDocument.responseHeaderDescriptor.map { it.field to it }
                                                        .toMap()

                                        document.responseHeaderDescriptor
                                                .forEach { h ->
                                                    if (!fieldMap.keys.contains(h.field))
                                                        oldDocument.responseHeaderDescriptor.add(h)
                                                }
                                    }
                                }

                                if (document.responseBodyDescriptors.isNotEmpty()) {
                                    if (oldDocument.responseBodyDescriptors.isEmpty())
                                        oldDocument.responseBodyDescriptors = document.responseBodyDescriptors
                                    else {
                                        val fieldMap =
                                                oldDocument.responseBodyDescriptors.map { it.path to it }
                                                        .toMap()

                                        document.responseBodyDescriptors
                                                .forEach { h ->
                                                    if (!fieldMap.keys.contains(h.path))
                                                        oldDocument.requestBodyDescriptor.add(h)
                                                }
                                    }
                                }

                                httpDocumentRepository.update(oldDocument)
                            }
                        } else {
                            httpDocumentRepository.save(document)
                        }
                    }
                }
            }

            if (hasDoc) {
                if (!resourceRepository.existsById(pkResource.id)) {
                    resourceRepository.save(pkResource)
                    savedPkResourceIds.add(pkResource.id!!)
                }
            }

        }

    }
}