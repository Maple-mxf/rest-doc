package restdoc.client.dubbo.test.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

    @PostMapping("/handshake/{pathVar}")
    Object handshake(@RequestBody Map<String,Object> param,
                     @PathVariable String pathVar,
                     HttpServletRequest request){

        System.err.println(param);
        System.err.println(pathVar);

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()){
            System.err.println(request.getHeader(headerNames.nextElement()));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("applicationType", "DUBBO");
        map.put("msg", "欢迎测试Http服务");

        return map;
    }
}
