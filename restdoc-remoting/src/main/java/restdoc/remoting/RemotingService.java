package restdoc.remoting;

public interface RemotingService {

    void start() throws InterruptedException;

    void restart() throws InterruptedException;

    void shutdown();

    void registerRPCHook(RPCHook rpcHook);

    default Status getStatus() {
        return Status.STOPPED;
    }
}
