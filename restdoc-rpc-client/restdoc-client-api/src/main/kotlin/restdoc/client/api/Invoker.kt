package restdoc.client.api

import restdoc.client.api.model.Invocation
import restdoc.client.api.model.InvocationResult


/**
 * The Invoker class provided invoke service abstract
 */
interface Invoker<T : Invocation, R> {


    /**
     * Invoke target service
     */
    fun invoke(t: T): InvocationResult<R>
}