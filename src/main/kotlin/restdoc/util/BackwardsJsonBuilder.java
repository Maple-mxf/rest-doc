package restdoc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import restdoc.core.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static restdoc.core.StandardKt.throwError;

/**
 * <p>逆向的JSON构建器</p>
 *
 * @since 2.0
 */
public class BackwardsJsonBuilder {

    private final ObjectMapper mapper = new ObjectMapper();

    private List<KeyValue> keyValues;

    private final Pattern fieldNamePattern = compile("[a-zA-Z0-9_]+[a-zA-Z0-9]*");

    private final Pattern indexPattern = compile("(\\[\\d+\\])+");

    private final Pattern nonNumberIndexPattern = compile("(\\[\\d*\\])+");

    private final Pattern arrayPattern = compile("[a-zA-Z0-9_]+[a-zA-Z0-9]*(\\[\\d*\\])+");

    private final ObjectNode jsonTree = mapper.createObjectNode();

    public BackwardsJsonBuilder(List<KeyValue> keyValues) {
        this.keyValues = this.findMinChildNodes(keyValues);
        build();
    }

    private void build() {
        for (KeyValue keyValue : this.keyValues) {
            putTreeValue(keyValue.getPath(), keyValue.getValue());
        }
    }


    private void putTreeValue(String path, Object value) {
        String[] childPaths = path.split("\\.");
        JsonNode jn = mapper.createObjectNode();

        for (int i = 0; i < childPaths.length; i++) {
            String childPath = childPaths[i];
            Matcher fieldMatcher = fieldNamePattern.matcher(childPath);

            if (fieldMatcher.find()) {
                String field = fieldMatcher.group(0);
                if (arrayPattern.matcher(childPath).find()) {
                    Matcher matcherIndex = nonNumberIndexPattern.matcher(childPath);
                    if (matcherIndex.find()) {
                        List<Integer> indexes = this.splitIndex(matcherIndex.group(1));
                        for (int index = 0; index < indexes.size(); index++) {
                            if (index == 0) {
                                ObjectNode _internal = (ObjectNode) jn;
                                ArrayNode arrayNode = mapper.createArrayNode();
                                _internal.putPOJO(field, arrayNode);
                                jn = arrayNode;
                                if (index == indexes.size() - 1) arrayNode.insertPOJO(indexes.get(0), value);
                            }
                            else if (index == indexes.size() - 1) {
                                ArrayNode arrayNode = (ArrayNode) jn;
                                arrayNode.insertPOJO(index, value);
                            }
                            else {
                                ArrayNode arrayNode = (ArrayNode) jn;
                                ArrayNode an = mapper.createArrayNode();
                                arrayNode.insert(index, an);
                                jn = an;
                            }
                        }
                    }
                    else {
                        throwError(Status.BAD_REQUEST, "含义不清的数据");
                    }
                }
                else {
                    if (jn instanceof ArrayNode) {
                        Matcher matcher = nonNumberIndexPattern.matcher(childPaths[i - 1]);
                        if (matcher.find()) {
                            List<Integer> indexes = this.splitIndex(matcher.group(1));
                            Integer lastIndex = indexes.get(indexes.size() - 1);

                            ObjectNode objectNode = mapper.createObjectNode();
                            ((ArrayNode) jn).insertPOJO(lastIndex, objectNode);

                            if (i == childPaths.length - 1) {
                                objectNode.putPOJO(childPath, value);
                                jn = objectNode;
                            }
                            else {
                                ObjectNode childNode = mapper.createObjectNode();
                                objectNode.putPOJO(childPath, childNode);
                                jn = childNode;
                            }
                        }
                    }
                    else {
                        if (i == childPaths.length - 1) {
                            ((ObjectNode) jn).putPOJO(childPath, value);
                        }
                        else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            ((ObjectNode) jn).putPOJO(childPath, objectNode);
                            jn = objectNode;
                        }
                    }
                }
            }
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

    private String escape(String string) {
        return string.replaceAll("\\.", "\\\\.")
                .replaceAll("\\[", "\\\\[")
                .replaceAll("\\]", "\\\\]");
    }

    private List<KeyValue> findMinChildNodes(List<KeyValue> dts) {
        return new ArrayList<>(dts.stream()
                .filter(t -> dts.stream()
                        .noneMatch(d -> d.getPath()
                                .matches(String.format("^%s\\.[a-zA-Z_]+[a-zA-Z0-9]*(.*)$",
                                        this.escape(t.getPath()))))
                )
                .collect(toMap(KeyValue::getPath, Function.identity(), (descriptor, descriptor2) -> descriptor2))
                .values());
    }

    public ObjectNode getJsonTree() {
        return jsonTree;
    }
}
