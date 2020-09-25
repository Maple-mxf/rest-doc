package restdoc.client.dubbo.test.service;

import java.util.HashMap;
import java.util.Map;

public interface EchoService {

    void echo(String a);

    default void echo1(String a) {
        System.err.println(String.format("restdoc.client.dubbo.test.service.EchoService.echo1 %s", a));
    }

    default void echo2(String a) {
        System.err.println(String.format("restdoc.client.dubbo.test.service.EchoService.echo2 %s", "Hello Kotlin"));
    }

    default void echo3(String a) {
        System.err.println(String.format("restdoc.client.dubbo.test.service.EchoService.echo3 %s", "Hello Java"));
    }

    default Map<String, Object> generateMap(String inParam) {
        Map<String, Object> map = new HashMap<>();
        map.put("inParam", "这是入参");
        map.put("echo", "Hello 欢迎测试远程Dubbo API");
        return map;
    }
}
