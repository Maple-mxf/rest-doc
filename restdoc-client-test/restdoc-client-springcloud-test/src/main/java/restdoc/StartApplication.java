package restdoc;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@SpringBootApplication
public class StartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class);
    }

    // restdoc-client-springcloud-test-1.0.RELEASE.jar

    @GetMapping("/healthEcho")
    Object healthEcho() {

        Map<Object, Object> map = new HashMap<>();
        map.put("code", "success");

        return map;
    }
}
