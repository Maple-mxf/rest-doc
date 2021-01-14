package restdoc.client.api;

import restdoc.client.api.model.Invocation;
import restdoc.client.api.model.InvocationResult;


/**
 * The Invoker class provided invoke service abstract
 *
 * @param <T> The Invocation type
 * @author Maple
 */
public interface Invoker<T extends Invocation> {

    /**
     * Invoke target service
     * @return Invocation result
     * @param t The Invocation instance
     */
    InvocationResult rpcInvoke(T t);
}
