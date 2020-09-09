package restdoc.web.core.schedule

import org.springframework.stereotype.Component
import restdoc.remoting.common.*
import java.util.concurrent.ConcurrentHashMap

@Component
class ExposedAPIManager {

    inner class APIKey(val address: String, val service: String)

    /**
     * Key is service name
     */
    private val restWebExposedAPI: MutableMap<APIKey, List<RestWebAPI>> = ConcurrentHashMap()

    /**
     *
     */
    private val dubboExposedAPI: MutableMap<APIKey, List<DubboAPI>> = ConcurrentHashMap()

    /**
     *
     */
    private val springcloudExposedAPI: MutableMap<APIKey, List<SpringCloudAPI>> = ConcurrentHashMap()

    fun registerAPI(type: ApplicationType, serviceAddress: String, service: String, apiList: List<ExposedAPI>) {
        when (type) {
            ApplicationType.DUBBO -> {
                dubboExposedAPI.putIfAbsent(APIKey(address = serviceAddress, service = service), apiList as List<DubboAPI>)
            }
            ApplicationType.REST_WEB -> {
                restWebExposedAPI.putIfAbsent(APIKey(address = serviceAddress, service = service), apiList as List<RestWebAPI>)
            }
            ApplicationType.SPRINGCLOUD -> {
                springcloudExposedAPI.putIfAbsent(APIKey(address = serviceAddress, service = service), apiList as List<SpringCloudAPI>)
            }
        }
    }

    fun listByAddress(type: ApplicationType, address: String): List<ExposedAPI> {
        return when (type) {
            ApplicationType.DUBBO -> {
                dubboExposedAPI
                        .filter { it.key.address == address }
                        .flatMap { it.value }
                        .toList()
            }
            ApplicationType.REST_WEB -> {
                restWebExposedAPI
                        .filter { it.key.address == address }
                        .flatMap { it.value }
                        .toList()
            }

            ApplicationType.SPRINGCLOUD -> {
                springcloudExposedAPI
                        .filter { it.key.address == address }
                        .flatMap { it.value }
                        .toList()
            }
        }
    }
}