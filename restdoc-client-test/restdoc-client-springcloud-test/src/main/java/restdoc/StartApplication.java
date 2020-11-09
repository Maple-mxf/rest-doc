package restdoc;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@SpringBootApplication
@Configuration
public class StartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class);
    }


    private int number;

    public int getNumber() {
        number++;
        return number;
    }

    @Bean
    Function<String, String> uppercase() {
        return String::toUpperCase;
    }

    // restdoc-client-springcloud-test-1.0.RELEASE.jar

    /*@GetMapping("/healthEcho")
    Object healthEcho() {

        Map<Object, Object> map = new HashMap<>();
        map.put("code", "success");

        return map;
    }*/

    @Bean
    public EndpointsListener endpointsListener(Environment environment) {
        return new EndpointsListener(environment);
    }
}
