package restdoc.client.dubbo.test;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.POST;

public class ClientTest {

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Add Headers
        List<String> s = new ArrayList<>();s.add("application/json;utf-8");
        headers.addAll("Content-Type",s);

        Map<String,Object> uriVars = new HashMap<>();
        uriVars.put("pathVar","pathVar");

        // Add body
        Map<String, Object> body = new HashMap<>();
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Object> responseEntity = restTemplate.exchange("/handshake/{pathVar}", POST, httpEntity, Object.class, uriVars);

        System.err.println(String.format("status code : %d", responseEntity.getStatusCodeValue()));
        System.err.println(String.format("content-type : %s", responseEntity.getHeaders().getContentType()));
        System.err.println(String.format("response result : %s", responseEntity.getBody()));
    }
}
