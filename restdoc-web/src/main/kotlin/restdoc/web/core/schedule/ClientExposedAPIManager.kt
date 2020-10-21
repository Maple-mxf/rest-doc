package restdoc.web.core.schedule

import org.springframework.stereotype.Component
import restdoc.remoting.common.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Overman
 */
@Deprecated(message = "ClientExposedAPIManager")
@Component
class ClientExposedAPIManager {

    /**
     * Key is service name
     */
    val restWebExposedExposedAPI: MutableMap<APIInfo, List<RestWebExposedAPI>> = ConcurrentHashMap()

    /**
     *
     */
    val dubboExposedExposedAPI: MutableMap<APIInfo, List<DubboExposedAPI>> = ConcurrentHashMap()

    /**
     *
     */
    val springcloudExposedExposedAPI: MutableMap<APIInfo, List<SpringCloudExposedAPI>> = ConcurrentHashMap()

    fun registerAPI(type: ApplicationType, serviceAddress: String, service: String, apiList: List<ExposedAPI>) {
        when (type) {
            ApplicationType.DUBBO -> {
                dubboExposedExposedAPI.putIfAbsent(APIInfo(remoteAddress = serviceAddress, service = service), apiList as List<DubboExposedAPI>)
            }
            ApplicationType.REST_WEB -> {
                restWebExposedExposedAPI.putIfAbsent(APIInfo(remoteAddress = serviceAddress, service = service), apiList as List<RestWebExposedAPI>)
            }
            ApplicationType.SPRINGCLOUD -> {
                springcloudExposedExposedAPI.putIfAbsent(APIInfo(remoteAddress = serviceAddress, service = service), apiList as List<SpringCloudExposedAPI>)
            }
        }
    }


    fun get(type: ApplicationType, service: String, remoteAddress: String): List<ExposedAPI> {
        return when (type) {
            ApplicationType.DUBBO -> {
                dubboExposedExposedAPI
                        .filter { it.key.service == service }
                        .filter { it.key.remoteAddress == remoteAddress }
                        .flatMap { it.value }
                        .toList()
            }
            ApplicationType.REST_WEB -> {
                restWebExposedExposedAPI
                        .filter { it.key.service == service }
                        .filter { it.key.remoteAddress == remoteAddress }
                        .flatMap { it.value }
                        .toList()
            }

            ApplicationType.SPRINGCLOUD -> {
                springcloudExposedExposedAPI
                        .filter { it.key.service == service }
                        .filter { it.key.remoteAddress == remoteAddress }
                        .flatMap { it.value }
                        .toList()
            }
        }
    }
}