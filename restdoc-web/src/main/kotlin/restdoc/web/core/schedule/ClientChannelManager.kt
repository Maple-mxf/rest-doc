package restdoc.web.core.schedule

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import restdoc.remoting.common.ApplicationClientInfo
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
class ClientChannelManager(var mapper: ObjectMapper) : CommandLineRunner {

    private val log: Logger = LoggerFactory.getLogger(ClientChannelManager::class.java)

    val clients: ConcurrentHashMap<String, ApplicationClientInfo> = ConcurrentHashMap()

    fun registerClient(id: String, applicationClientInfo: ApplicationClientInfo?) {
        clients.put(id, applicationClientInfo!!)
    }

    fun unregisterClient(id: String) {
        clients.remove(id)
    }

    fun findClient(id: String?): ApplicationClientInfo? {
        return clients[id]
    }

    fun findClientByRemoteAddress(address: String): ApplicationClientInfo? {
        return clients.filter { it.value.clientId == address }.map { it.value }.first()
    }

    fun list(): List<ApplicationClientInfo?> {
        return ArrayList(clients.values)
    }

    @Throws(Exception::class) override fun run(vararg args: String?) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                try {
                    log.info("clients: {} ", mapper.writeValueAsString(clients))
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }, 1000L, 5000L)
    }

}