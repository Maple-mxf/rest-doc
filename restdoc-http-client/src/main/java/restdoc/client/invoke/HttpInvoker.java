package restdoc.client.invoke;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import restdoc.remoting.common.body.HttpCommunicationCaptureBody;

import java.util.Map;
import java.util.Objects;

public class HttpInvoker {

    private final RestTemplate restTemplate;

    private final int port;

    private final String contextPath;

    public HttpInvoker(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;
        port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("server.port")));
        contextPath = environment.getProperty("server.servlet.context-path");
    }

    public ResponseEntity<Object> execute(HttpCommunicationCaptureBody capture) {
        String url = this.autocompleteURL(capture.getUrl());
        capture.setCompleteUrl(url);
        HttpEntity<Map<String, Object>> httpEntity =
                new HttpEntity<>(capture.getRequestBody(), capture.getRequestHeaders());

        return restTemplate.exchange(url, capture.getMethod(), httpEntity, Object.class, capture.getUriVariables());
    }


    private String autocompleteURL(String originURL) {
        if (originURL.startsWith("http") || originURL.startsWith("https")) return originURL;
        if (originURL.startsWith(contextPath)) {
            return String.format("http://127.0.0.1:%d%s", port, originURL);
        } else {
            if (originURL.startsWith("/")) {
                return String.format("http://127.0.0.1:%d%s%s", port, contextPath, originURL);
            } else {
                return String.format("http://127.0.0.1:%d%s/%s", port, contextPath, originURL);
            }
        }
    }
}
