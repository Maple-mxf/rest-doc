package restdoc.remoting;

import restdoc.remoting.netty.ResponseFuture;

public interface InvokeCallback {
    void operationComplete(final ResponseFuture responseFuture) throws InterruptedException;
}
