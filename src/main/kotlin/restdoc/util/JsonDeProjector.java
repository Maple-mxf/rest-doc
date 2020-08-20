package restdoc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import restdoc.core.Status;
import restdoc.model.BodyFieldDescriptor;
import restdoc.model.FieldType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.String.format;
import static restdoc.core.StandardKt.throwError;

/**
 * <p>The JsonDeProjector provided deProject json string content to bodyDescriptor</p>
 *
 * @author ubuntu-m
 * @see JsonProjector
 * @since 1.0
 */
public class JsonDeProjector {

    private final List<BodyFieldDescriptor> descriptors = new ArrayList<>();

    private JsonNode jsonNode;

    public JsonDeProjector(String content) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonNode = mapper.readTree(content);
        } catch (Throwable e) {
            throwError(Status.BAD_REQUEST, format("json转换异常%s", e.getMessage()));
        }
    }

    public List<BodyFieldDescriptor> deProject() {
        Iterator<String> iterator = jsonNode.fieldNames();
        while (iterator.hasNext()) {
            String field = iterator.next();
            this.findChildren("", field, this.jsonNode);
        }
        return this.descriptors;
    }

    /**
     * @param keyPath   prefix json key path
     * @param key       json field key
     * @param valueNode jsonNode
     */
    private void findChildren(String keyPath, String key, JsonNode valueNode) {
        if (valueNode instanceof ArrayNode) {
            ArrayNode an = (ArrayNode) valueNode;
            for (int i = 0; i < an.size(); i++) {
                findChildren(format("%s[%s]", keyPath, i), null, an.get(i));
            }
        }
        else if (valueNode instanceof ObjectNode) {
            ObjectNode on = (ObjectNode) valueNode;
            JsonNode jsonNode = on.get(key);
            if (jsonNode instanceof ArrayNode) {
                this.add(format("%s.%s", keyPath, key), null, FieldType.ARRAY);
                ArrayNode an = (ArrayNode) jsonNode;
                for (int i = 0; i < an.size(); i++) {
                    findChildren(format("%s[%s]", keyPath, i), null, an.get(i));
                }
            }
            else if (jsonNode instanceof ObjectNode) {
                this.add(format("%s.%s", keyPath, key), null, FieldType.OBJECT);
                Iterator<String> iterator = jsonNode.fieldNames();
                while (iterator.hasNext()) {
                    String field = iterator.next();
                    findChildren(format("%s.%s", keyPath, key), field, jsonNode.get(field));
                }
            }
            else if (jsonNode instanceof BooleanNode) {
                this.add(format("%s.%s", keyPath, key), jsonNode.booleanValue(), FieldType.BOOLEAN);
            }
            else if (jsonNode instanceof NumericNode) {
                this.add(format("%s.%s", keyPath, key), jsonNode.numberValue(), FieldType.NUMBER);
            }
            else if (jsonNode instanceof TextNode) {
                this.add(format("%s.%s", keyPath, key), jsonNode.textValue(), FieldType.STRING);
            }
            else {
                this.add(format("%s.%s", keyPath, key), null, FieldType.MISSING);
            }
        }
    }

    private void add(String path, Object value, FieldType type) {
        BodyFieldDescriptor descriptor = new BodyFieldDescriptor(
                path, value, "", type, false, "NoDefaultValue");
        this.descriptors.add(descriptor);
    }
}
