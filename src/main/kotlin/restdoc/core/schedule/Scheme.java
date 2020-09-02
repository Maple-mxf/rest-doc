package restdoc.core.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Scheme provided message format
 * <p>
 * {@code
 * <p>
 * {
 * "scheme":1,
 * "result":{...}
 * }
 * <p>
 * }
 */
class Scheme {

    private static final int HTTP_TASK_EXECUTE_RESULT = 1;

    private static ObjectMapper mapper = new ObjectMapper();

    public JsonNode map(byte[] bytes) throws IOException {
        JsonNode value = mapper.readValue(bytes, JsonNode.class);
        if (value.get("scheme").asInt(HTTP_TASK_EXECUTE_RESULT) == HTTP_TASK_EXECUTE_RESULT) {
            return value;
        }
        return null;
    }
}
