package restdoc.web.schedule

import io.netty.channel.Channel
import restdoc.remoting.InvokeCallback
import restdoc.remoting.exception.RemotingSendRequestException
import restdoc.remoting.exception.RemotingTimeoutException
import restdoc.remoting.netty.NettyRemotingServer
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.remoting.protocol.RemotingSysResponseCode
import restdoc.rpc.client.common.model.ApplicationClientInfo
import restdoc.rpc.client.common.model.ApplicationType
import restdoc.web.util.MD5Util
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

/**
 * ClientState
 */
enum class ClientState {
    Idle,
    Stopped,
    Started
}

enum class OS {
    Linux,
    Windows,
    Mac
}

interface ClientCallback {
    fun call(client: Client)
}

interface Client {

    fun id(): String

    fun host(): String

    fun port(): Int

    fun os(): OS

    fun hostName(): String

    fun state(): ClientState

    fun start(): Boolean

    fun idle(): Boolean

    fun stop(): Boolean

    fun echo(): Long

    @Throws(RemotingSendRequestException::class, InterruptedException::class, RemotingTimeoutException::class)
    fun <R> execute(command: RemotingCommand, type: Class<R>, timeoutMillis: Long): RemotingCommand

    @Throws(RemotingSendRequestException::class, InterruptedException::class, RemotingTimeoutException::class)
    fun <R> executeExpectType(command: RemotingCommand, type: Class<R>, timeoutMillis: Long): R

    @Throws(RemotingSendRequestException::class, InterruptedException::class, RemotingTimeoutException::class)
    fun executeAsync(command: RemotingCommand, timeoutMillis: Long, callback: InvokeCallback)

    fun beforeIdle(): List<ClientCallback>

    fun afterIdle(): List<ClientCallback>

    fun beforeStopped(): List<ClientCallback>

    fun afterStopped(): List<ClientCallback>

    fun beforeStarted(): List<ClientCallback>

    fun afterStarted(): List<ClientCallback>

    fun at(): ApplicationType
}

class ClientAdapter(private val remotingServer: NettyRemotingServer,
                    private val info: ApplicationClientInfo) : Client {

    val at: ApplicationType = info.applicationType
    val id = id()

    private val channel: Channel = info.channel
    private val isa: InetSocketAddress = channel.remoteAddress() as InetSocketAddress
    private var state: ClientState = ClientState.Stopped
    private val beforeIdleCallbacks = ArrayList<ClientCallback>()
    private val afterIdleCallbacks = ArrayList<ClientCallback>()
    private val beforeStoppedCallbacks = ArrayList<ClientCallback>()
    private val afterStoppedCallbacks = ArrayList<ClientCallback>()
    private val beforeStartedCallbacks = ArrayList<ClientCallback>()
    private val afterStartedCallbacks = ArrayList<ClientCallback>()

    override fun id(): String = MD5Util.MD5Encode(info.id, StandardCharsets.UTF_8.name())
    override fun host(): String = isa.address.hostAddress
    override fun port(): Int = isa.port
    override fun os(): OS {
        return when {
            info.osname.contains("Windows") -> OS.Windows
            info.osname.contains("Linux") || info.osname.contains("Centos") -> OS.Linux
            else -> OS.Mac
        }
    }
    override fun hostName(): String = info.hostname
    override fun state(): ClientState = state
    override fun beforeIdle(): List<ClientCallback> = beforeIdleCallbacks
    override fun afterIdle(): List<ClientCallback> = afterIdleCallbacks
    override fun beforeStopped(): List<ClientCallback> = beforeStoppedCallbacks
    override fun afterStopped(): List<ClientCallback> = afterStoppedCallbacks
    override fun beforeStarted(): List<ClientCallback> = beforeStartedCallbacks
    override fun afterStarted(): List<ClientCallback> = afterStartedCallbacks

    @Throws(RemotingSendRequestException::class, InterruptedException::class, RemotingTimeoutException::class)
    override fun <R> execute(command: RemotingCommand, type: Class<R>, timeoutMillis: Long): RemotingCommand {
        val response = remotingServer.invokeSync(channel, command, timeoutMillis)
        if (response.code == RemotingSysResponseCode.SUCCESS)
            return response
        throw RemotingSendRequestException("request faild. code ${response.code}, remark ${response.remark}")
    }

    @Throws(RemotingSendRequestException::class, InterruptedException::class, RemotingTimeoutException::class)
    override fun <R> executeExpectType(command: RemotingCommand, type: Class<R>, timeoutMillis: Long): R {
        val response = execute(command, type, timeoutMillis)
        return RemotingSerializable.decode(response.body, type)
    }

    @Throws(RemotingSendRequestException::class, InterruptedException::class, RemotingTimeoutException::class)
    override fun executeAsync(command: RemotingCommand, timeoutMillis: Long, callback: InvokeCallback) =
            remotingServer.invokeAsync(channel, command, timeoutMillis, callback)

    override fun start(): Boolean {
        executeCallback(this.beforeStartedCallbacks)
        this.state = ClientState.Started
        executeCallback(this.afterStartedCallbacks)
        return true
    }

    private fun executeCallback(callbacks: List<ClientCallback>) {
        for (callback in callbacks) callback.call(this)
    }

    override fun idle(): Boolean {
        executeCallback(this.beforeIdleCallbacks)
        this.state = ClientState.Idle
        executeCallback(this.afterIdleCallbacks)
        return true
    }

    override fun stop(): Boolean {
        executeCallback(this.beforeStoppedCallbacks)
        this.state = ClientState.Stopped
        executeCallback(this.afterStoppedCallbacks)
        return true
    }


    override fun echo(): Long {
        return 0L
    }

    override fun at(): ApplicationType = info.applicationType

    init {
        // afterStartedCallbacks.add(ClientCallback);
    }
}
