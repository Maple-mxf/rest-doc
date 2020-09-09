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
    private val restWebExposedExposedAPI: MutableMap<APIKey, List<RestWebExposedAPI>> = ConcurrentHashMap()

    /**
     *
     */
    private val dubboExposedExposedAPI: MutableMap<APIKey, List<DubboExposedAPI>> = ConcurrentHashMap()

    /**
     *
     */
    private val springcloudExposedExposedAPI: MutableMap<APIKey, List<SpringCloudExposedAPI>> = ConcurrentHashMap()

    fun registerAPI(type: ApplicationType, serviceAddress: String, service: String, apiList: List<ExposedAPI>) {
        when (type) {
            ApplicationType.DUBBO -> {
                dubboExposedExposedAPI.putIfAbsent(APIKey(address = serviceAddress, service = service), apiList as List<DubboExposedAPI>)
            }
            ApplicationType.REST_WEB -> {
                restWebExposedExposedAPI.putIfAbsent(APIKey(address = serviceAddress, service = service), apiList as List<RestWebExposedAPI>)
            }
            ApplicationType.SPRINGCLOUD -> {
                springcloudExposedExposedAPI.putIfAbsent(APIKey(address = serviceAddress, service = service), apiList as List<SpringCloudExposedAPI>)
            }
        }
    }

    fun listByAddress(type: ApplicationType, address: String): List<ExposedAPI> {
        return when (type) {
            ApplicationType.DUBBO -> {
                dubboExposedExposedAPI
                        .filter { it.key.address == address }
                        .flatMap { it.value }
                        .toList()
            }
            ApplicationType.REST_WEB -> {
                restWebExposedExposedAPI
                        .filter { it.key.address == address }
                        .flatMap { it.value }
                        .toList()
            }

            ApplicationType.SPRINGCLOUD -> {
                springcloudExposedExposedAPI
                        .filter { it.key.address == address }
                        .flatMap { it.value }
                        .toList()
            }
        }
    }
}