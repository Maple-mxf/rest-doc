package restdoc.web.util.dp;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import restdoc.web.util.FieldType;
import restdoc.web.util.Node;
import restdoc.web.util.PathValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;

/**
 * Project JSON/XML
 */
public abstract class BaseProjector<R> implements Projector<R> {



    // Filter Json array field
    protected final static Pattern ARRAY_PATTERN = compile("([a-zA-Z0-9_]+[a-zA-Z0-9_\\-]*)(\\[\\d*\\])+");

    // Filter the field
    protected final static String FIELD_REGEX = "^[a-zA-Z0-9_]+[a-zA-Z0-9]*$";

    public abstract R project();

    // Whole Node
    protected final List<Node> nodes = new ArrayList<>();

    // Nodes
    protected Node nodeTree = new Node("root", "", FieldType.OBJECT, new ArrayList<>());

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
        List<Node> parentNodes = this.nodes.stream()
                .filter(t -> t.getPath().matches(FIELD_REGEX))
                .collect(toList());

        for (Node parentNode : parentNodes) {
            findChildren(parentNode);
            this.nodeTree.getChildren().add(parentNode);
        }
    }

    protected void findChildren(Node parentNode) {
        String regexValue = String.format("^%s\\.[a-zA-Z_]+[a-zA-Z0-9_]*$", BaseProjector.escape(parentNode.getPath()));
        String regexArray = String.format("^%s(\\[\\d*\\])$", BaseProjector.escape(parentNode.getPath()));

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

    static List<Integer> splitIndex(String indexString) {
        List<String> indexStrings = Arrays.stream(indexString.split("\\]"))
                .map(t -> t.replaceAll("\\[", ""))
                .collect(toList());

        return indexStrings.stream()
                .map(t -> t.isEmpty() ? 0 : parseInt(t))
                .collect(toList());
    }


    static String escape(String string) {
        return string.replaceAll("\\.", "\\\\.")
                .replaceAll("\\[", "\\\\[")
                .replaceAll("\\]", "\\\\]");
    }

}
