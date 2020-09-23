package restdoc.remoting;

import restdoc.remoting.exception.RemotingException;

public interface RemotingService {

    void start() throws InterruptedException, RemotingException;

    void shutdown();

    void registerRPCHook(RPCHook rpcHook);

}
