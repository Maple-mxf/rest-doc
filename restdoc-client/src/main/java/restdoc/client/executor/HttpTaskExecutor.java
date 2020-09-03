package restdoc.client.executor;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import restdoc.remoting.common.body.SubmitHttpTaskRequestBody;

import java.util.Map;
import java.util.Objects;

public class HttpTaskExecutor {

    private final RestTemplate restTemplate;

    private final int port;

    private final String contextPath;

    public HttpTaskExecutor(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;
        port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("server.port")));
        contextPath = environment.getProperty("server.servlet.context-path");
    }

    public ResponseEntity<Object> execute(SubmitHttpTaskRequestBody submitHttpTaskRequestBody) {

        String url = this.autocompleteURL(submitHttpTaskRequestBody.getUrl());
        HttpMethod method = HttpMethod.resolve(submitHttpTaskRequestBody.getMethod().toUpperCase());
        HttpHeaders headers = constructHeaders(submitHttpTaskRequestBody.getHeader());
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(submitHttpTaskRequestBody.getBody(), headers);

        return restTemplate.exchange(url, method, httpEntity, Object.class, submitHttpTaskRequestBody.getUriVar());
    }

    private HttpHeaders constructHeaders(Map<String, String> header) {
        HttpHeaders httpHeaders = new HttpHeaders();
        header.forEach(httpHeaders::add);
        return httpHeaders;
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
