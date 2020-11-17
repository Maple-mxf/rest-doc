package restdoc.web.util.dp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import restdoc.web.core.Status;
import restdoc.web.util.FieldType;
import restdoc.web.util.Node;
import restdoc.web.util.PathValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static restdoc.web.core.StandardKt.throwError;

/**
 * <p>The JsonProjector provided project the flatten json descriptor to json object</p>
 *
 * <p>Project rule</p>
 *
 * <ul>
 *     <li>Each key must be start with English alphabet</li>
 *     <li>Json Array must be end with brackets '[' or ']' ,Index in square brackets is optional. sample: users[] equivalent to users[0]</li>
 *     <li>If json array No index,default index is zero</li>
 * </ul>
 *
 *
 * <p>sample</p>
 *
 * <pre>
 *     {@code
 *
 *          ObjectNode jsonTree = new JsonProjector(Lists.newArrayList(
 *                 new PathValue("users[1][2].name", "value")
 *         )).getJsonTree();
 *     }
 *
 * operation result As follows json code
 *
 * {@code
 * {
 *   "users": [
 *     [
 *       {
 *         "name": "value"
 *       }
 *     ]
 *   ]
 *  }
 * }
 * </pre>
 *
 * @author Overman
 * @since 1.0
 */
public class JsonProjector {

    private final ObjectMapper mapper = new ObjectMapper();

    // Whole Node
    private List<Node> nodes = new ArrayList<>();

    // Nodes
    private Node nodeTree = new Node("root", "", FieldType.OBJECT, new ArrayList<>());

    // Filter the field
    private final static String FIELD_REGEX = "^[a-zA-Z0-9_]+[a-zA-Z0-9]*$";

    // Filter the field
    private final static Pattern FIELD_PATTERN = compile("[a-zA-Z0-9_]+[a-zA-Z0-9]*");

    // Filter Json array index
    private final static Pattern INDEX_PATTERN = compile("(\\[\\d+\\])+");

    // Filter non number Json array index(default index = 0)
    private final static Pattern NON_NUMBER_INDEX_PATTERN = compile("(\\[\\d*\\])+");

    // Filter Json array field
    private final static Pattern ARRAY_PATTERN = compile("([a-zA-Z0-9_]+[a-zA-Z0-9]*)(\\[\\d*\\])+");

    // Filter end with ']'
    // private final static Pattern CHILD_ARRAY_PATTERN = compile("^(.*)\\[\\d+\\]$");

    /**
     * Convert given flatten json descriptor to node tree {@link Node}
     *
     * <pre>
     *     users
     *       ^
     *       |
     *     users[1]
     *       ^
     *       |
     *     users[1][1]
     *       ^
     *       |
     *     users[1][1][1]
     *      /   \
     *    name   age
     * </pre>
     *
     * @param pathValues Given flatten path and json descriptor
     */
    public JsonProjector(List<PathValue> pathValues) {

        // Resolve the json
        List<PathValue> pathValueList = resolve(pathValues);

        // Build for rootNode
        this.buildForTreeNode(pathValueList);
    }

    public ObjectNode project() {
        // After project. mapping a json tree
        return this.buildForJsonNode();
    }

    public Map<String, Object> projectToMap() {
        return mapper.convertValue(this.project(), Map.class);
    }

    /**
     * Resolve the given path values
     *
     * <pre>
     *     Before resolve:
     *
     *     {@code
     *     new JsonProjector(Lists.newArrayList(new PathValue("users[0][1][2]", "value")));
     *     }
     *
     *     After resolve:
     *  [
     *   {
     *     "path": "users",
     *     "value": null
     *   },
     *   {
     *     "path": "users[0]",
     *     "value": null
     *   },
     *   {
     *     "path": "users[0][1]",
     *     "value": null
     *   },
     *   {
     *     "path": "users[0][1][2]",
     *     "value": "value"
     *   }
     * ]
     * </pre>
     * <p>
     * <p>
     * Deduplication by {@link PathValue#getPath()}
     * <p>
     * TODO  why Deduplication?
     * <p>
     * Review The resolve code
     */
    @VisibleForTesting
    public static List<PathValue> resolve(List<PathValue> pathValues) {

        return pathValues.stream()
                .flatMap(t -> {
                    String[] pathArray = t.getPath().split("\\.");

                    return IntStream.range(0, pathArray.length)
                            .mapToObj(i -> {
                                String path = String.join(".", Arrays.copyOfRange(pathArray, 0, i + 1));
                                return (i < pathArray.length - 1) ?
                                        new PathValue(path, null) :
                                        new PathValue(path, t.getValue());
                            });
                })
                .flatMap(e -> {

                    String[] pathArray = e.getPath().split("\\.");
                    String lastField = pathArray[pathArray.length - 1];

                    // If last key is array
                    Matcher matcher = ARRAY_PATTERN.matcher(lastField);

                    if (matcher.find()) {
                        String field = matcher.group(1);
                        List<Integer> indexes = splitIndex(lastField.substring(field.length()));

                        pathArray[pathArray.length - 1] = field;

                        List<String> indices = IntStream.range(0, indexes.size())
                                .mapToObj(index ->
                                {
                                    String suffixIndices = indexes.subList(0, index + 1)
                                            .stream()
                                            .map(i -> String.format("[%d]", i))
                                            .collect(Collectors.joining(""));

                                    return String.join("", Joiner.on(".").join(pathArray), suffixIndices);
                                })
                                .collect(toList());

                        return IntStream.range(0, indices.size())
                                .mapToObj(i ->
                                        (i < indices.size() - 1) ?
                                                new PathValue(indices.get(i), null) :
                                                new PathValue(indices.get(i), e.getValue())

                                );
                    } else {
                        return Stream.of(e);
                    }
                })
                .peek(t -> t.setPath(t.getPath().replaceAll("\\[\\]", "[0]")))

                // Bad code.
                .collect(Collectors.groupingBy(PathValue::getPath))
                .entrySet()
                .stream()
                .map(entry -> new PathValue(entry.getKey(), entry.getValue().get(0).getValue()))
                .collect(toList());
    }

    /**
     * Build tree node
     *
     * @param pathValues after resolve path values {@link JsonProjector#resolve(List)}
     * @see Node
     */
    @VisibleForTesting
    protected void buildForTreeNode(List<PathValue> pathValues) {
        this.nodes.addAll(pathValues.stream()
                .map(pv -> new Node(pv.getPath(), pv.getValue(), FieldType.OBJECT, new ArrayList<>()))
                .collect(toList()));

        // Find First level node
        List<Node> parentNodes = nodes.stream()
                .filter(t -> t.getPath().matches(FIELD_REGEX))
                .collect(toList());

        for (Node parentNode : parentNodes) {
            findChildren(parentNode);
            this.nodeTree.getChildren().add(parentNode);
        }
    }

    /**
     * Build json node
     *
     * @see JsonNode
     */
    @VisibleForTesting
    protected ObjectNode buildForJsonNode() {
        List<Node> children = this.nodeTree.getChildren();
        ObjectNode on = mapper.createObjectNode();

        for (Node child : children) {
            ObjectNode childNode = new ChildJsonBuilder(child).getObjectNode();
            List<Map.Entry<String, JsonNode>> entries = Lists.newArrayList(childNode.fields());
            if (!entries.isEmpty()) {
                String key = entries.get(0).getKey();
                JsonNode value = entries.get(0).getValue();
                on.putPOJO(key, value);
            }
        }
        return on;
    }

    private class ChildJsonBuilder {
        private final ObjectNode objectNode = mapper.createObjectNode();

        private ChildJsonBuilder(Node parentNode) {
            this.build(parentNode, objectNode);
        }

        ObjectNode getObjectNode() {
            return this.objectNode;
        }

        private void build(Node pn, JsonNode dn) {
            List<Node> children = pn.getChildren();
            String[] fields = pn.getPath().split("\\.");
            String lastField = fields[fields.length - 1];

            if (children.isEmpty()) {
                if (dn instanceof ObjectNode) {
                    ((ObjectNode) dn).putPOJO(lastField, pn.getValue());
                } else if (dn instanceof ArrayNode) {
                    Matcher matcher = ARRAY_PATTERN.matcher(lastField);
                    if (matcher.find()) {
                        String field = matcher.group(1);
                        List<Integer> indexes = splitIndex(lastField.substring(field.length()));
                        int lastIndex = indexes.get(indexes.size() - 1);
                        ((ArrayNode) dn).insertPOJO(lastIndex, pn.getValue());
                    }
                } else {
                    throwError(Status.INTERNAL_SERVER_ERROR);
                }
            } else {
                FieldType pnType = FieldType.OBJECT;

                if (children.stream().allMatch(node -> node.getPath().matches(String.format("^%s\\[\\d*\\]$", escape(pn.getPath()))))) {
                    pnType = FieldType.ARRAY;
                }

                if (dn instanceof ObjectNode) {
                    if (pnType == FieldType.OBJECT) {
                        ObjectNode on = mapper.createObjectNode();
                        ((ObjectNode) dn).putPOJO(lastField, on);
                        dn = on;
                    } else {
                        ArrayNode an = mapper.createArrayNode();
                        ((ObjectNode) dn).putPOJO(lastField, an);
                        dn = an;
                    }
                } else if (dn instanceof ArrayNode) {
                    Matcher matcher = ARRAY_PATTERN.matcher(lastField);
                    if (matcher.find()) {
                        String field = matcher.group(1);
                        List<Integer> indexes = splitIndex(lastField.substring(field.length()));
                        int lastIndex = indexes.get(indexes.size() - 1);

                        if (pnType == FieldType.OBJECT) {
                            ObjectNode on = mapper.createObjectNode();
                            ((ArrayNode) dn).insertPOJO(lastIndex, on);
                            dn = on;
                        } else {
                            ArrayNode an = mapper.createArrayNode();
                            ((ArrayNode) dn).insertPOJO(lastIndex, an);
                            dn = an;
                        }
                    } else {
                        throwError(Status.INTERNAL_SERVER_ERROR);
                    }
                }
                for (Node child : children) {
                    build(child, dn);
                }
            }
        }
    }

    private void findChildren(Node parentNode) {
        String regexValue = String.format("^%s\\.[a-zA-Z_]+[a-zA-Z0-9_]*$", escape(parentNode.getPath()));
        String regexArray = String.format("^%s(\\[\\d*\\])$", escape(parentNode.getPath()));

        List<Node> children = this.nodes.stream()
                .filter(node -> {
                    boolean matchesValue = node.getPath().matches(regexValue);
                    boolean matchesArray = node.getPath().matches(regexArray);
                    return matchesArray || matchesValue;
                })
                .collect(toList());
        parentNode.getChildren().addAll(children);

        for (Node child : children) {
            findChildren(child);
        }
    }

    private static List<Integer> splitIndex(String indexString) {
        List<String> indexStrings = Arrays.stream(indexString.split("\\]"))
                .map(t -> t.replaceAll("\\[", ""))
                .collect(toList());

        return indexStrings.stream()
                .map(t -> t.isEmpty() ? 0 : parseInt(t))
                .collect(toList());
    }

    private static String escape(String string) {
        return string.replaceAll("\\.", "\\\\.")
                .replaceAll("\\[", "\\\\[")
                .replaceAll("\\]", "\\\\]");
    }
}
