package restdoc.client.dubbo.test.base;

import org.junit.Test;
import restdoc.client.dubbo.test.service.EchoService;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ReflectTest {

    @Test
    public void testGetParameterName(){
        for (Method method : EchoService.class.getMethods()) {
            method.setAccessible(true);

            Parameter[] parameters = method.getParameters();

            for (Parameter parameter : parameters) {
                System.err.println(parameter.getName());
            }
        }
    }
}
