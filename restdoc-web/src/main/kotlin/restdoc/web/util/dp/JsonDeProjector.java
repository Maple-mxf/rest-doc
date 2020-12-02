package restdoc.web.util.dp;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.util.Assert;
import restdoc.web.core.Status;
import restdoc.web.model.BodyFieldDescriptor;
import restdoc.web.util.FieldType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;


/**
 * <p>The JsonDeProjector provided deProject json string content to bodyDescriptor</p>
 *
 * @author Maple
 * @see JsonProjector
 * @since 1.0
 */
public class JsonDeProjector implements DeProjector {

    private final List<BodyFieldDescriptor> descriptors = new ArrayList<>();

    private JsonNode jsonNode;

    private List<Node> nodes = new ArrayList<>();

    public JsonDeProjector(JsonNode jsonNode) {
        Assert.notNull(jsonNode, "被deProject的对象不可为空");
        this.jsonNode = jsonNode;
    }

    /**
     * DSL Intermediate
     */
    static class Node {
        public String path, field;
        public FieldType type, parentType;
        public transient List<Node> children;
        public Object value;
        public int childIndex;

        public Node(String path,
                    String field,
                    FieldType type,
                    FieldType parentType) {
            this.path = path;
            this.field = field;
            this.type = type;
            this.parentType = parentType;
            // Setup default index value
            if (FieldType.ARRAY.equals(type)) childIndex = 0;
        }
    }

    @Override
    public List<BodyFieldDescriptor> deProject() {

        // 1 Translate
        this.translate();

        // 2 Mapper
        this.mapToDescriptor();

        // 3 End Return
        return this.descriptors;
    }


    private void mapToDescriptor() {
        List<BodyFieldDescriptor> descriptors = this.nodes
                .stream()
                .map(node -> new BodyFieldDescriptor(
                        node.path.replaceFirst("root\\.", ""), node.value,
                        null, node.type,
                        false, node.value))
                .filter(d -> !d.getType().equals(FieldType.ARRAY)
                        && !d.getType().equals(FieldType.OBJECT))
                .collect(Collectors.toList());

        this.descriptors.addAll(descriptors);
    }

    /**
     * Translate given the json value
     *
     * @see Node
     */
    private void translate() {
        if (jsonNode instanceof ObjectNode) {
            this.translateObject(new Node("root", "root",
                    FieldType.OBJECT,
                    FieldType.MISSING), (ObjectNode) jsonNode);
        } else if (jsonNode instanceof ArrayNode)
            this.translateArray(new Node("root[0]", "root",
                    FieldType.ARRAY,
                    FieldType.MISSING), (ArrayNode) jsonNode);
        else {
            Status.BAD_REQUEST.error();
        }
    }

    /**
     * Translate the given json array
     */
    private void translateArray(Node parentNode, ArrayNode an) {
        for (int index = 0; index < an.size(); index++) {
            JsonNode node = an.get(index);

            String path = parentNode.path.substring(0, parentNode.path.lastIndexOf("["));
            path = String.format("%s[%d]", path, index);

            if (node instanceof ObjectNode) {
                Node childNode = new Node(
                        path,
                        null,
                        FieldType.OBJECT,
                        FieldType.ARRAY);

                this.nodes.add(childNode);

                this.translateObject(childNode, (ObjectNode) node);

            } else if (node instanceof ArrayNode) {

                Node childNode = new Node(
                        path,
                        null,
                        FieldType.ARRAY,
                        FieldType.ARRAY);

                this.nodes.add(childNode);

                this.translateArray(childNode, (ArrayNode) node);

            } else {

                Node childNode = new Node(
                        path,
                        null,
                        FieldType.MISSING,
                        FieldType.ARRAY);

                this.nodes.add(childNode);

                translateBaseType(childNode, node);
            }
        }
    }

    /**
     * Translate the give json object
     */
    private void translateObject(Node parentNode, ObjectNode on) {

        Iterator<String> fields = on.fieldNames();

        while (fields.hasNext()) {

            String field = fields.next();
            JsonNode node = on.get(field);

            if (node instanceof ObjectNode) {
                Node childNode = new Node(format("%s.%s", parentNode.path, field),
                        field,
                        FieldType.OBJECT,
                        FieldType.OBJECT);

                this.nodes.add(childNode);

                this.translateObject(childNode, (ObjectNode) node);
            } else if (node instanceof ArrayNode) {
                Node childNode = new Node(format("%s.%s", parentNode.path, field),
                        field,
                        FieldType.ARRAY,
                        FieldType.OBJECT);

                this.nodes.add(childNode);

                Node startChildNode = new Node(
                        format("%s[%d]", childNode.path, childNode.childIndex),
                        childNode.field,
                        FieldType.ARRAY,
                        FieldType.OBJECT
                );

                this.translateArray(startChildNode, (ArrayNode) node);
            } else {
                Node childNode = new Node(
                        String.format("%s.%s", parentNode.path, field),
                        null,
                        FieldType.MISSING,
                        FieldType.OBJECT);

                this.nodes.add(childNode);

                this.translateBaseType(childNode, node);
            }
        }
    }

    private void translateBaseType(Node parentNode, JsonNode node) {
        if (node instanceof NumericNode) {
            parentNode.value = node.numberValue();
            parentNode.type = FieldType.NUMBER;
        } else if (node instanceof BooleanNode) {
            parentNode.value = node.booleanValue();
            parentNode.type = FieldType.BOOLEAN;
        } else if (node instanceof MissingNode) {
            parentNode.value = null;
            parentNode.type = FieldType.MISSING;
        } else if (node instanceof TextNode) {
            parentNode.value = node.textValue();
            parentNode.type = FieldType.STRING;
        } else if (node instanceof NullNode) {
            parentNode.value = null;
            parentNode.type = FieldType.MISSING;
        }
    }
}
