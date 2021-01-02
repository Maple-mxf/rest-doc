package restdoc.client.api;

import restdoc.client.api.model.Invocation;
import restdoc.client.api.model.InvocationResult;


/**
 * The Invoker class provided invoke service abstract
 *
 * @author Maple
 */
public interface Invoker<T extends Invocation> {

    /**
     * Invoke target service
     */
    InvocationResult rpcInvoke(T t);
}
