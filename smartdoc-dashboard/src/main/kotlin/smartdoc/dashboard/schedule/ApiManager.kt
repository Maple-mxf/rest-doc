package smartdoc.dashboard.schedule

import com.google.common.collect.Table
import com.google.common.collect.Tables
import org.springframework.stereotype.Component
import restdoc.rpc.client.common.model.ApiDescriptor
import restdoc.rpc.client.common.model.ApplicationType
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

/**
 * ApiManager
 */
interface ApiManager {

    fun add(clientId: String, at: ApplicationType, apiDescriptors: List<ApiDescriptor>): Boolean

    fun remove(clientId: String, at: ApplicationType): Boolean

    fun list(): Map<String, Map<ApplicationType, List<ApiDescriptor>>>

    fun list(clientId: String): Map<ApplicationType, List<ApiDescriptor>>

    fun list(clientId: String, at: ApplicationType): List<ApiDescriptor>
}

@Component("apiManagerAdapterImpl")
open class ApiManagerAdapterImpl : ApiManager {

    private val table: Table<String, ApplicationType, List<ApiDescriptor>> =
            Tables.newCustomTable(
                    ConcurrentHashMap<String, ConcurrentHashMap<ApplicationType, List<ApiDescriptor>>>()
                            as Map<String, MutableMap<ApplicationType, List<ApiDescriptor>>>?)
            { ConcurrentHashMap<ApplicationType, List<ApiDescriptor>>() }


    override fun add(clientId: String, at: ApplicationType, apiDescriptors: List<ApiDescriptor>): Boolean {
        if (table.containsRow(clientId) && table.containsColumn(at) && table.containsValue(apiDescriptors))
            throw IllegalArgumentException("Api descriptors existed")
        table.put(clientId, at, apiDescriptors)
        return true
    }

    override fun remove(clientId: String, at: ApplicationType): Boolean = table.remove(clientId, at) != null

    override fun list(): Map<String, Map<ApplicationType, List<ApiDescriptor>>> = HashMap(table.rowMap())

    override fun list(clientId: String): Map<ApplicationType, List<ApiDescriptor>> {
        if (!table.containsRow(clientId)) throw IllegalArgumentException("client no api :${clientId}")
        return HashMap(table.row(clientId))
    }

    override fun list(clientId: String, at: ApplicationType): List<ApiDescriptor> {
        if (!table.containsRow(clientId)) throw IllegalArgumentException("client no api :${clientId}")
        return ArrayList(table.get(clientId, at))
    }
}