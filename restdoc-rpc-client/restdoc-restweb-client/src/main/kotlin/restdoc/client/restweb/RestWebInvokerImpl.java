package restdoc.client.restweb;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import restdoc.client.api.Invoker;
import restdoc.client.api.model.InvocationResult;
import restdoc.client.api.model.RestWebInvocation;
import restdoc.client.api.model.RestWebInvocationResult;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RestWebInvokerImpl
 */
public class RestWebInvokerImpl implements Invoker<RestWebInvocation> {

    private final RestTemplate restTemplate;

    private final int port;

    private final String contextPath;


    public RestWebInvokerImpl(Environment environment, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.contextPath = environment.getProperty("server.servlet.context-path", "");
        this.port = Integer.parseInt(environment.getProperty("server.port", "8080"));
    }

    @NotNull
    @Override
    public InvocationResult rpcInvoke(@NotNull RestWebInvocation invocation) {
        String url = autocompleteURL(invocation.url);
        HttpHeaders requestHeaders = new HttpHeaders();
        invocation.requestHeaders.forEach(requestHeaders::addAll);
        HttpEntity httpEntity = new HttpEntity(invocation.requestBody, requestHeaders);

        ResponseEntity<Object> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.valueOf(invocation.method), httpEntity, Object.class, invocation.uriVariable);

            Map<String, List<String>> responseHeaders = responseEntity.getHeaders().entrySet()
                    .stream()
                    .map(hd -> new AbstractMap.SimpleEntry<>(hd.getKey(), hd.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return new RestWebInvocationResult(
                    true,
                    null,
                    invocation,
                    responseEntity.getStatusCodeValue(),
                    responseHeaders,
                    responseEntity.getBody());

        } catch (RestClientResponseException e) {
            String exceptionMessage;
            if (e instanceof HttpServerErrorException.BadGateway)
                exceptionMessage = "BadGateway";
            else if (e instanceof HttpClientErrorException.BadRequest)
                exceptionMessage = "BadRequest";
            else if (e instanceof HttpClientErrorException.Conflict)
                exceptionMessage = "Conflict";
            else if (e instanceof HttpClientErrorException.Forbidden)
                exceptionMessage = "Forbidden";
            else if (e instanceof HttpServerErrorException.GatewayTimeout)
                exceptionMessage = "GatewayTimeout";
            else if (e instanceof HttpClientErrorException.Gone)
                exceptionMessage = "Gone";
            else if (e instanceof HttpClientErrorException.NotFound)
                exceptionMessage = "NotFound";
            else if (e instanceof HttpClientErrorException.MethodNotAllowed)
                exceptionMessage = "MethodNotAllowed";
            else if (e instanceof HttpClientErrorException.NotAcceptable)
                exceptionMessage = "NotAcceptable";
            else if (e instanceof HttpClientErrorException.UnsupportedMediaType)
                exceptionMessage = "UnsupportedMediaType";
            else if (e instanceof HttpClientErrorException.UnprocessableEntity)
                exceptionMessage = "UnprocessableEntity";
            else if (e instanceof HttpClientErrorException.TooManyRequests)
                exceptionMessage = "TooManyRequests";
            else if (e instanceof HttpClientErrorException.Unauthorized)
                exceptionMessage = "Unauthorized";
            else if (e instanceof HttpServerErrorException.InternalServerError)
                exceptionMessage = "InternalServerError";
            else if (e instanceof HttpServerErrorException.NotImplemented)
                exceptionMessage = "NotImplemented";
            else if (e instanceof HttpServerErrorException.ServiceUnavailable)
                exceptionMessage = "ServiceUnavailable";
            else exceptionMessage = String.format("Unknown Error:%s", e.getMessage());

            return new RestWebInvocationResult(false, exceptionMessage, invocation, e.getRawStatusCode(),
                    new HashMap<>(), null);
        } catch (RuntimeException rex) {
            return new RestWebInvocationResult(false, String.format("Unkown Error:%s", rex.getMessage()), invocation, -1, new HashMap<>(), null);
        }
    }

    private String autocompleteURL(String originURL) {
        if (originURL.startsWith("http") || originURL.startsWith("https")) return originURL;
        if (originURL.startsWith(contextPath))
            return String.format("http://127.0.0.1:%d%s", port, originURL);
        else if (originURL.startsWith("/"))
            return String.format("http://127.0.0.1:%d%s%s", port, contextPath, originURL);
        else
            return String.format("http://127.0.0.1:%d%s/%s", port, contextPath, originURL);
    }
}
