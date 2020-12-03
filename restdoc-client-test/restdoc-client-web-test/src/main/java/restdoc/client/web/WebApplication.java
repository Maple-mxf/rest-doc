package restdoc.client.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@Import(value = {RestWebAgentClientConfiguration.class})
//@EnableConfigurationProperties(value = {AgentConfigurationProperties.class})
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class);
    }
}
