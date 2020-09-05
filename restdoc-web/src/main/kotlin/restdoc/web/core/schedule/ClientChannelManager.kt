package restdoc.web.core.schedule

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import restdoc.remoting.ClientChannelInfo
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
class ClientChannelManager(var mapper: ObjectMapper) : CommandLineRunner {

    private val log: Logger = LoggerFactory.getLogger(ClientChannelManager::class.java)

    val clients: ConcurrentHashMap<String, ClientChannelInfo> = ConcurrentHashMap()

    fun registerClient(id: String, clientChannelInfo: ClientChannelInfo?) {
        clients.putIfAbsent(id, clientChannelInfo!!)
    }

    fun unregisterClient(id: String) {
        clients.remove(id)
    }

    fun findClient(id: String?): ClientChannelInfo? {
        return clients[id]
    }

    fun list(): List<ClientChannelInfo?>{
        return ArrayList(clients.values)
    }

    @Throws(Exception::class) override fun run(vararg args: String?) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                try {
                    log.info("clients: {} ", mapper.writeValueAsString(clients))
                } catch (e: JsonProcessingException) {
                    e.printStackTrace()
                }
            }
        }, 1000L, 5000L)
    }

}