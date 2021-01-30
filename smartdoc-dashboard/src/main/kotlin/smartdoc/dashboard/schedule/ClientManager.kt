package smartdoc.dashboard.schedule

import io.netty.channel.Channel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import restdoc.remoting.netty.NettyRemotingServer
import restdoc.rpc.client.common.model.ApplicationClientInfo
import restdoc.rpc.client.common.model.ApplicationType
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

interface ClientManager {

    fun list(): Collection<Client>

    fun list(at: ApplicationType): Collection<Client>

    fun size(): Int

    fun add(info: ApplicationClientInfo, remotingServer: NettyRemotingServer): Client

    fun add(client: Client): Boolean

    fun remove(id: String): Boolean

    fun get(id: String): Client?

    fun get(channel: Channel): Client?

    fun schedule(id: String, tw: RemotingTaskWrapper): Any?
}

@Component("clientManagerAdapterImpl")
open class ClientManagerAdapter : ClientManager {

    @Autowired
    lateinit var apiManager: ApiManager

    private val adapters: MutableMap<String, Client> = ConcurrentHashMap()

    override fun list(): Collection<Client> = ArrayList(adapters.values)
    override fun list(at: ApplicationType): Collection<Client> = list().filter { it.applicationType() == at }
    override fun size(): Int = adapters.size

    override fun add(client: Client): Boolean {
        adapters[client.id()] = client
        return true
    }

    override fun add(info: ApplicationClientInfo, remotingServer: NettyRemotingServer): Client {
        val adapter = ClientAdapter(generateId(info.channel, info.applicationType), info, remotingServer, apiManager)
        this.add(adapter)
        adapter.start()
        return adapter
    }

    override fun remove(id: String): Boolean = adapters.remove(id) != null
    override fun get(id: String): Client? = adapters[id]
    override fun get(channel: Channel): Client? = adapters.values.find { it.channel() == channel }
    override fun schedule(id: String, tw: RemotingTaskWrapper): Any? {
        val adapter = adapters[id] ?: throw IllegalArgumentException("Client not exist: $id")
        return if (tw.async) {
            adapter.executeAsync(tw.command!!, tw.timeoutMills, tw.callback)
            null
        } else {
            adapter.executeExpectType(tw.command!!, tw.responseType!!, tw.timeoutMills)
        }
    }

    private fun generateId(channel: Channel, at: ApplicationType): String {
        val remote = channel.remoteAddress() as InetSocketAddress
        return smartdoc.dashboard.util.MD5Util.MD5Encode("${remote.address.hostAddress}/${at.name}", StandardCharsets.UTF_8.name())
    }
}
