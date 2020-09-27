package restdoc.client.dubbo.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Overman
 * @since 2020/9/27
 */
@RestController
public class EchoHttpController {

    @GetMapping(value = "/echo")
    public Object echoRestWeb() {
        Map<String, Object> map = new HashMap<>();
        map.put("applicationType", "DUBBO");
        map.put("msg", "欢迎测试Http服务");
        return map;
    }
}
