package restdoc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import restdoc.model.BodyFieldDescriptor;
import restdoc.model.FieldType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

/**
 * <p>The JsonDeProjector provided deProject json string content to bodyDescriptor</p>
 *
 * @author ubuntu-m
 * @see JsonProjector
 * @since 1.0
 */
public class JsonDeProjector {

    private final List<BodyFieldDescriptor> descriptors = new ArrayList<>();

    private final static String NULL_KEY = null;

    private JsonNode jsonNode;

    public JsonDeProjector(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public List<BodyFieldDescriptor> deProject() {
        Iterator<String> iterator = jsonNode.fieldNames();
        while (iterator.hasNext()) {
            String key = iterator.next();
            this.findChildren("", key, this.jsonNode);
        }
        return this.descriptors;
    }


    private void findChildren(String keyPath, String key, JsonNode valueNode) {

        if (valueNode instanceof ArrayNode) {
            ArrayNode an = (ArrayNode) valueNode;
            for (int i = 0; i < an.size(); i++) {
                keyPath = format("%s.%s[%s]", keyPath, key, i);
                findChildren(keyPath, NULL_KEY, an.get(i));
            }
        } else if (valueNode instanceof ObjectNode) {
            ObjectNode on = (ObjectNode) valueNode;

            // Array
            if (Objects.equals(key, NULL_KEY)) {
                Iterator<String> iterator = on.fieldNames();
                while (iterator.hasNext()) {
                    String field = iterator.next();
                    findChildren(keyPath, field, on);
                }
            }
            // JSON
            else {
                keyPath = keyPath.isEmpty() ? key : format("%s.%s", keyPath, key);

                JsonNode jn = on.get(key);
                if (jn instanceof ArrayNode) {
                    this.add(keyPath, null, FieldType.ARRAY);
                    ArrayNode an = (ArrayNode) jn;
                    for (int i = 0; i < an.size(); i++) {
                        keyPath = format("%s[%s]", keyPath, i);
                        this.add(String.format("%s", keyPath), an.get(i));
                        findChildren(keyPath, NULL_KEY, an.get(i));
                    }
                } else if (jn instanceof ObjectNode) {
                    this.add(keyPath, null, FieldType.OBJECT);
                    ObjectNode one = (ObjectNode) jn;
                    Iterator<String> iterator = one.fieldNames();
                    while (iterator.hasNext()) {
                        String field = iterator.next();
                        this.add(String.format("%s.%s", keyPath, field), one.get(field));
                        findChildren(keyPath, field, one.get(field));
                    }
                } else if (jn instanceof BooleanNode) {
                    this.add(keyPath, jn.booleanValue(), FieldType.BOOLEAN);
                } else if (jn instanceof NumericNode) {
                    this.add(keyPath, jn.numberValue(), FieldType.NUMBER);
                } else if (jn instanceof TextNode) {
                    this.add(keyPath, jn.textValue(), FieldType.STRING);
                } else {
                    this.add(keyPath, null, FieldType.MISSING);
                }
            }
        }
    }

    private void add(String path, JsonNode valueNode) {
        TypeValue typeValue = getType(valueNode);
        this.add(path, typeValue.value, typeValue.type);
    }


    private static class TypeValue {
        public FieldType type;
        public Object value;
        public boolean primitive;

        public TypeValue(FieldType type, Object value, boolean primitive) {
            this.type = type;
            this.value = value;
            this.primitive = primitive;
        }
    }

    private TypeValue getType(JsonNode valueNode) {
        if (valueNode instanceof ObjectNode)
            return new TypeValue(FieldType.OBJECT, null, false);
        else if (valueNode instanceof ArrayNode)
            return new TypeValue(FieldType.ARRAY, null, false);
        else if (valueNode instanceof TextNode)
            return new TypeValue(FieldType.STRING, valueNode.textValue(), true);
        else if (valueNode instanceof NumericNode)
            return new TypeValue(FieldType.NUMBER, valueNode.numberValue(), true);
        else if (valueNode instanceof BooleanNode)
            return new TypeValue(FieldType.BOOLEAN, valueNode.booleanValue(), true);
        else return new TypeValue(FieldType.MISSING, null, true);
    }

    private void add(String path, Object value, FieldType type) {
        BodyFieldDescriptor descriptor = new BodyFieldDescriptor(
                path, value, "", type, false, "NoDefaultValue");
        this.descriptors.add(descriptor);
    }
}
