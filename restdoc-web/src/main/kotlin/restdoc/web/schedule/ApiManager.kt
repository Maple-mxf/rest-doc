package restdoc.web.schedule

import restdoc.rpc.client.common.model.ApiDescriptor
import restdoc.rpc.client.common.model.ApplicationType

/**
 * ApiManager
 */
interface ApiManager {

    fun add(clientId: String, at: ApplicationType, apiDescriptors: List<ApiDescriptor>): Boolean

    fun remove(): Boolean

    fun list(): Map<String, Map<ApplicationType, List<ApiDescriptor>>>

    fun list(clientId: String): Map<ApplicationType, List<ApiDescriptor>>

    fun list(clientId: String, at: ApplicationType): List<ApiDescriptor>
}