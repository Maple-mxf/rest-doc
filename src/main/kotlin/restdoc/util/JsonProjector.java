package restdoc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import restdoc.model.FieldType;
import restdoc.model.Node;
import restdoc.model.PathValue;

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

/**
 * @since 2.0
 */
public class JsonProjector {

    private final ObjectMapper mapper = new ObjectMapper();

    // All Node
    private List<Node> nodes = new ArrayList<>();

    // Nodes
    private Node nodeTree = new Node("root", "", FieldType.OBJECT, new ArrayList<>());

    // After project. mapping a json tree
    private final ObjectNode jsonTree;

    // Filter the field
    private final String fieldRegex = "^[a-zA-Z0-9_]+[a-zA-Z0-9]*$";

    // Filter the field
    private final Pattern fieldPattern = compile("[a-zA-Z0-9_]+[a-zA-Z0-9]*");

    // Filter Json array index
    private final Pattern indexPattern = compile("(\\[\\d+\\])+");

    // Filter non number Json array index(default index = 0)
    private final Pattern nonNumberIndexPattern = compile("(\\[\\d*\\])+");

    // Filter Json array field
    private final static Pattern arrayPattern = compile("([a-zA-Z0-9_]+[a-zA-Z0-9]*)(\\[\\d*\\])+");

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

        // Build for rootNode
        this.buildForTreeNode(this.resolve(pathValues));

        // Build for jsonNode
        this.jsonTree = this.buildForJsonNode();
    }

    public ObjectNode getJsonTree() {
        return this.jsonTree;
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
     */
    @VisibleForTesting
    protected List<PathValue> resolve(List<PathValue> pathValues) {
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

                    Matcher matcher = arrayPattern.matcher(lastField);

                    if (matcher.find()) {
                        String field = matcher.group(1);
                        List<Integer> indexes = this.splitIndex(lastField.substring(field.length()));

                        List<String> indices = IntStream.range(0, indexes.size())
                                .mapToObj(index ->
                                {
                                    String suffixIndices = indexes.subList(0, index + 1)
                                            .stream()
                                            .map(i -> String.format("[%s]", i))
                                            .collect(Collectors.joining(""));

                                    return String.join("", field, suffixIndices);
                                })
                                .collect(toList());

                        List<String> arr = new ArrayList<>();
                        arr.add(field);
                        arr.addAll(indices);

                        return IntStream.range(0, arr.size())
                                .mapToObj(i ->
                                        (i < arr.size() - 1) ?
                                                new PathValue(arr.get(i), null) :
                                                new PathValue(arr.get(i), e.getValue())

                                );
                    }
                    else {
                        return Stream.of(e);
                    }
                }).collect(toList());
    }

    /**
     * Build tree node
     *
     * @param pathValues after resolve path values {@link JsonProjector#resolve(List)}
     * @see Node
     */
    @VisibleForTesting
    protected void buildForTreeNode(List<PathValue> pathValues) {

        this.nodes.addAll(FluentIterable.from(pathValues)
                .transform(pv -> new Node(pv.getPath(), pv.getValue(), FieldType.OBJECT, new ArrayList<>()))
                .toList());

        // Find First level node
        List<Node> parentNodes = nodes.stream()
                .filter(t -> t.getPath().matches(fieldRegex))
                .collect(toList());

        for (Node parentNode : parentNodes) {
            findChildren(parentNode);
            this.nodeTree.getChildren().add(parentNode);
        }
    }

    /**
     * Build json node
     *
     * @see com.fasterxml.jackson.databind.JsonNode
     */
    @VisibleForTesting
    protected ObjectNode buildForJsonNode() {
        List<Node> children = this.nodeTree.getChildren();
        ObjectNode on = mapper.createObjectNode();

        for (Node child : children) {
            ObjectNode childNode = this.buildForChildJsonNode(child);
            ArrayList<Map.Entry<String, JsonNode>> entries = Lists.newArrayList(childNode.fields());

            if (!entries.isEmpty()) {
                String key = entries.get(0).getKey();
                JsonNode value = entries.get(0).getValue();
                on.putPOJO(key, value);
            }
        }
        return on;
    }

    /**
     * Build json for child node
     */
    private ObjectNode buildForChildJsonNode(Node parentNode) {
        ObjectNode objectNode = mapper.createObjectNode();

        Node pn = parentNode;

        while (pn != null && !pn.getChildren().isEmpty()) {
        }

        return objectNode;
    }


    private void findChildren(Node parentNode) {
        String regexValue = String.format("^%s\\.[a-zA-Z_]+[a-zA-Z0-9_]*$", escape(parentNode.getPath()));
        String regexArray = String.format("^%s(\\[\\d+\\])$", escape(parentNode.getPath()));

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

    private List<Integer> splitIndex(String indexString) {
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
