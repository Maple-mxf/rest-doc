package restdoc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import restdoc.core.Status;

import static restdoc.core.StandardKt.throwError;

/**
 * @author ubuntu-m
 * @see JsonProjector
 * @since 2.0
 */
public class JsonDeProjector {

    private final ObjectMapper mapper = new ObjectMapper();

    private JsonNode jsonNode;

    public JsonDeProjector(String content) {
        try {
            jsonNode = mapper.readTree(content);
        } catch (Throwable e) {
            throwError(Status.BAD_REQUEST, String.format("json转换异常%s", e.getMessage()));
        }
    }
}
