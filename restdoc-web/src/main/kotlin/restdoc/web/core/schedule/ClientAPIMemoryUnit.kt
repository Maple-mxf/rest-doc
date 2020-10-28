package restdoc.web.core.schedule

import org.springframework.stereotype.Component
import restdoc.remoting.common.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Overman
 */
@Component
class ClientAPIMemoryUnit {

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


    /**
     * 当客户端与服务端建立连接时调用此方法
     */
    @Deprecated(message = "封装过于僵硬")
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

    fun unregisterAPI(apiInfos: List<APIInfo>) {
        apiInfos.forEach { dubboExposedExposedAPI.remove(it) }
        apiInfos.forEach { restWebExposedExposedAPI.remove(it) }
        apiInfos.forEach { springcloudExposedExposedAPI.remove(it) }
    }

    /**
     * 当客户端断开连接  注销客户端所产生的数据
     */
    @Deprecated(message = "封装过于僵硬")
    fun unregisterAPI(type: ApplicationType, serviceAddress: String, service: String) {
        val apiInfo = APIInfo(remoteAddress = serviceAddress, service = service)
        when (type) {
            ApplicationType.DUBBO -> {
                dubboExposedExposedAPI.remove(apiInfo)
            }
            ApplicationType.REST_WEB -> {
                restWebExposedExposedAPI.remove(apiInfo)
            }
            ApplicationType.SPRINGCLOUD -> {
                springcloudExposedExposedAPI.remove(apiInfo)
            }
        }
    }

    fun getByClient(remoteAddress: String): MutableList<APIInfo> {
        val webAPIKeys = restWebExposedExposedAPI
                .filterKeys { it.remoteAddress == remoteAddress }
                .map { k -> k.key }

        val dubboAPIKeys = dubboExposedExposedAPI
                .filterKeys { it.remoteAddress == remoteAddress }
                .map { k -> k.key }

        val springcloudAPIKeys = springcloudExposedExposedAPI
                .filterKeys { it.remoteAddress == remoteAddress }
                .map { k -> k.key }

        val keys = mutableListOf<APIInfo>()
        keys.addAll(webAPIKeys)
        keys.addAll(dubboAPIKeys)
        keys.addAll(springcloudAPIKeys)

        return keys
    }

    fun get(type: ApplicationType, remoteAddress: String): List<ExposedAPI> {
        return when (type) {
            ApplicationType.DUBBO -> {
                dubboExposedExposedAPI
                        .filter { it.key.remoteAddress == remoteAddress }
                        .flatMap { it.value }
                        .toList()
            }
            ApplicationType.REST_WEB -> {
                restWebExposedExposedAPI
                        .filter { it.key.remoteAddress == remoteAddress }
                        .flatMap { it.value }
                        .toList()
            }

            ApplicationType.SPRINGCLOUD -> {
                springcloudExposedExposedAPI
                        .filter { it.key.remoteAddress == remoteAddress }
                        .flatMap { it.value }
                        .toList()
            }
        }
    }


    @Deprecated(message = "封装过于僵硬")
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