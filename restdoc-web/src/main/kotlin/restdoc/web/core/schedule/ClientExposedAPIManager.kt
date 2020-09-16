package restdoc.web.core.schedule

import org.springframework.stereotype.Component
import restdoc.remoting.common.*
import java.util.concurrent.ConcurrentHashMap

@Component
class ClientExposedAPIManager {

    inner class APIContext(val address: String, val service: String)

    /**
     * Key is service name
     */
    private val restWebExposedExposedAPI: MutableMap<APIContext, List<RestWebExposedAPI>> = ConcurrentHashMap()

    /**
     *
     */
    open val dubboExposedExposedAPI: MutableMap<APIContext, List<DubboExposedAPI>> = ConcurrentHashMap()

    /**
     *
     */
    private val springcloudExposedExposedAPI: MutableMap<APIContext, List<SpringCloudExposedAPI>> = ConcurrentHashMap()

    fun registerAPI(type: ApplicationType, serviceAddress: String, service: String, apiList: List<ExposedAPI>) {
        when (type) {
            ApplicationType.DUBBO -> {
                dubboExposedExposedAPI.putIfAbsent(APIContext(address = serviceAddress, service = service), apiList as List<DubboExposedAPI>)
            }
            ApplicationType.REST_WEB -> {
                restWebExposedExposedAPI.putIfAbsent(APIContext(address = serviceAddress, service = service), apiList as List<RestWebExposedAPI>)
            }
            ApplicationType.SPRINGCLOUD -> {
                springcloudExposedExposedAPI.putIfAbsent(APIContext(address = serviceAddress, service = service), apiList as List<SpringCloudExposedAPI>)
            }
        }
    }


    @Deprecated(message = "")
    fun listBy(type: ApplicationType, service: String): List<ExposedAPI> {
        return when (type) {
            ApplicationType.DUBBO -> {
                dubboExposedExposedAPI
                        .filter { it.key.service == service }
                        .flatMap { it.value }
                        .toList()
            }
            ApplicationType.REST_WEB -> {
                restWebExposedExposedAPI
                        .filter { it.key.service == service }
                        .flatMap { it.value }
                        .toList()
            }

            ApplicationType.SPRINGCLOUD -> {
                springcloudExposedExposedAPI
                        .filter { it.key.service == service }
                        .flatMap { it.value }
                        .toList()
            }
        }
    }
}