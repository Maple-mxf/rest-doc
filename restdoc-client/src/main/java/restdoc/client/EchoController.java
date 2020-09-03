package restdoc.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

@RestController
public class EchoController {

    @GetMapping(value = "/echo", produces = MediaType.APPLICATION_JSON_VALUE)
    Object echo() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("serviceName", "restdoc-starter");
        map.put("time", new Date().getTime());
        map.put("success", true);
        map.put("system", "windows");
        map.put("tcp model", "nio channel");
        map.put("tcp framework", "netty-4.0.Final");
        return map;
    }
}
