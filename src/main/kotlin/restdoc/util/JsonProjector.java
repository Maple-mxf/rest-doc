package restdoc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.VisibleForTesting;
import restdoc.model.Node;
import restdoc.model.PathValue;

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
 * @since 2.0
 */
public class JsonProjector {

    private final ObjectMapper mapper = new ObjectMapper();

    // Nodes
    private List<Node> nodeTree;

    // Filter the field
    private final Pattern fieldPattern = compile("[a-zA-Z0-9_]+[a-zA-Z0-9]*");

    // Filter Json array index
    private final Pattern indexPattern = compile("(\\[\\d+\\])+");

    // Filter non number Json array index(default index = 0)
    private final Pattern nonNumberIndexPattern = compile("(\\[\\d*\\])+");

    // Filter Json array field
    private final static Pattern arrayPattern = compile("([a-zA-Z0-9_]+[a-zA-Z0-9]*)(\\[\\d*\\])+");

    private final ObjectNode jsonTree = mapper.createObjectNode();

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
        try {
            System.err.println(mapper.writeValueAsString(this.resolve(pathValues)));
        } catch (Throwable e) {
            e.printStackTrace();
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
     */
    @VisibleForTesting
    protected List<PathValue> resolve(List<PathValue> pathValues) {
        return pathValues.stream()
                .flatMap(t -> {
                    String[] pathArray = t.getPath().split("\\.");
                    return IntStream.range(0, pathArray.length)
                            .mapToObj(i ->
                                    (i < pathArray.length - 1) ?
                                            new PathValue(pathArray[i], null) :
                                            new PathValue(pathArray[i], t.getValue()));
                })
                .flatMap(e -> {
                    Matcher matcher = arrayPattern.matcher(e.getPath());
                    if (matcher.find()) {

                        String field = matcher.group(1);
                        List<Integer> indexes = this.splitIndex(e.getPath().substring(field.length()));

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


    private List<Integer> splitIndex(String indexString) {
        List<String> indexStrings = Arrays.stream(indexString.split("\\]"))
                .map(t -> t.replaceAll("\\[", ""))
                .collect(toList());

        return indexStrings.stream()
                .map(t -> t.isEmpty() ? 0 : parseInt(t))
                .collect(toList());
    }

    private String escape(String string) {
        return string.replaceAll("\\.", "\\\\.")
                .replaceAll("\\[", "\\\\[")
                .replaceAll("\\]", "\\\\]");
    }
}
