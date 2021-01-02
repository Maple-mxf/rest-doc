package restdoc.client.dubbo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restdoc.client.api.Invoker;
import restdoc.client.api.model.DubboInvocation;
import restdoc.client.api.model.DubboInvocationResult;
import restdoc.client.api.model.InvocationResult;
import restdoc.client.api.model.ObjectHolder;

import java.lang.reflect.Method;

/**
 * Dubbo invoker impl
 *
 * @see org.apache.dubbo.rpc.RpcInvocation
 */
@Component
public class DubboInvokerImpl implements Invoker<DubboInvocation> {
    private ObjectMapper mapper = new ObjectMapper();

    private final DubboRefBeanManager beanManager;

    @Autowired
    public DubboInvokerImpl(DubboRefBeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public InvocationResult rpcInvoke(DubboInvocation invocation) {
        Object bean = beanManager.getRefBean(invocation.getRefName());
        try {
            Class[] paramTypes = invocation.getParameters()
                    .stream()
                    .map(pa -> {
                        try {
                            return Class.forName(pa.className);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e.getMessage());
                        }
                    }).toArray(Class[]::new);

            Object[] parameters = invocation.getParameters()
                    .stream()
                    .map(this::mappingValue)
                    .toArray(Object[]::new);

            Method method = bean.getClass().getMethod(invocation.getMethodName(), paramTypes);
            method.setAccessible(true);

            Object returnValue = method.invoke(bean, parameters);

            String serializedReturnValue = returnValue == null ?
                    "" : mapper.writeValueAsString(returnValue);
            return new DubboInvocationResult(
                    false,
                    null,
                    invocation,
                    serializedReturnValue,
                    method.getReturnType().toString());

        } catch (Exception e) {
            e.printStackTrace();
            return new DubboInvocationResult(
                    false,
                    e.getMessage(),
                    invocation,
                    "",
                    Void.class.toString());
        }
    }

    private Object mappingValue(ObjectHolder<Object> holder) {
        Class type = ReflectUtils.forName(holder.className);
        if (ReflectUtils.isPrimitive(type)) {
            return holder.value;
        } else {
            return mapper.convertValue(holder.value, type);
        }
    }

}
