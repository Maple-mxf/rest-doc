package restdoc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import restdoc.model.BodyFieldDescriptor;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.util.Comparator.comparingInt;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.*;


public class JsonParser {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Collection<BodyFieldDescriptor> descriptors;

    final ObjectNode on = mapper.createObjectNode();

    public JsonParser(List<BodyFieldDescriptor> descriptors) {
        this.descriptors = this.getMinNode(descriptors);
        parse1();
    }

    private List<BodyFieldDescriptor> getMinNode(List<BodyFieldDescriptor> dts) {

        return new ArrayList<>(dts.stream()
                .filter(t -> dts.stream()
                        .noneMatch(d -> d.getPath()
                                .matches(String.format("^%s\\.[a-zA-Z_]+[a-zA-Z0-9]*(.*)$",
                                        this.escape(t.getPath()))))
                )
                .collect(toMap(BodyFieldDescriptor::getPath, Function.identity(), (descriptor, descriptor2) -> descriptor2))
                .values());
    }

    public void parse1() {
        for (BodyFieldDescriptor descriptor : this.descriptors) {
            this.buildPOJO(descriptor.getPath(), descriptor.getValue());
        }
    }


    @Deprecated
    public void parse(String parentPath) {
        List<BodyFieldDescriptor> children = getChildren(parentPath);

        for (BodyFieldDescriptor child : children) {
            buildPOJO(child.getPath(), child.getValue());
            parse(child.getPath());
        }
    }


    private List<BodyFieldDescriptor> getChildren(String parentPath) {
        if (parentPath == null || parentPath.isEmpty()) {

            return this.descriptors
                    .stream()
                    .collect(groupingBy(descriptor -> descriptor.getPath().split("\\.").length))
                    .entrySet()
                    .stream()
                    .min(comparingInt(Map.Entry::getKey))
                    .map(Map.Entry::getValue)
                    .orElse(new ArrayList<>());

        }
        else {
            return descriptors.stream()
                    .filter(descriptor ->
                            compile(
                                    String.format("^%s\\.[a-zA-Z]+[a-zA-Z0-9]*(\\[\\d+\\])*$", this.escape(parentPath)))
                                    .matcher(descriptor.getPath()).find())
                    .collect(toList());
        }
    }

    final Pattern fieldNamePattern = compile("[a-zA-Z0-9_]+[a-zA-Z0-9]*");

    private String escape(String string) {
        return string.replaceAll("\\.", "\\\\.")
                .replaceAll("\\[", "\\\\[")
                .replaceAll("\\]", "\\\\]");
    }


    private void buildPOJO(String path, Object value) {
        String[] childPaths = path.split("\\.");
        JsonNode jn = this.on;

        for (int i = 0; i < childPaths.length; i++) {
            String ph = childPaths[i];
            Matcher fieldMatcher = fieldNamePattern.matcher(ph);

            if (fieldMatcher.find()) {
                String field = fieldMatcher.group(0);
                boolean isArray = ph.endsWith("]");
                if (isArray) {
                    Matcher matcher = compile("(\\[\\d+\\])+").matcher(ph);
                    if (matcher.find()) {
                        List<String> indexes = Arrays.stream(matcher.group(1).split("\\]"))
                                .map(t -> t.replaceAll("\\[", ""))
                                .collect(toList());

                        for (int index = 0; index < indexes.size(); index++) {
                            // First Element
                            if (index == 0) {
                                ObjectNode _internal = (ObjectNode) jn;
                                ArrayNode arrayNode = mapper.createArrayNode();
                                _internal.putPOJO(field, arrayNode);
                                jn = arrayNode;
                                continue;
                            }
                            // Last Element
                            else if (index == indexes.size() - 1) {
                                ArrayNode arrayNode = (ArrayNode) jn;
                                arrayNode.insertPOJO(index, value);
                                continue;
                            }
                            // Middle Element
                            ArrayNode arrayNode = (ArrayNode) jn;
                            ArrayNode an = mapper.createArrayNode();
                            arrayNode.insert(index, an);
                            jn = an;
                        }
                    }
                }
                else {
                    if (jn instanceof ArrayNode) {
                        Matcher matcher = compile("(\\[\\d+\\])+").matcher(childPaths[i - 1]);
                        if (matcher.find()) {
                            List<String> indexes = Arrays.stream(matcher.group(1).split("\\]"))
                                    .map(t -> t.replaceAll("\\[", ""))
                                    .collect(toList());

                            String lastIndex = indexes.get(indexes.size() - 1);
                            ObjectNode objectNode = mapper.createObjectNode();
                            ((ArrayNode) jn).insertPOJO(parseInt(lastIndex), objectNode);

                            if (i == childPaths.length - 1) {
                                objectNode.putPOJO(ph, value);
                                jn = objectNode;
                            }
                            else {
                                ObjectNode childNode = mapper.createObjectNode();
                                objectNode.putPOJO(ph, childNode);
                                jn = childNode;
                            }
                        }
                    }
                    else {
                        if (i == childPaths.length - 1) {
                            ((ObjectNode) jn).putPOJO(ph, value);
                        }
                        else {
                            ObjectNode objectNode = mapper.createObjectNode();
                            ((ObjectNode) jn).putPOJO(ph, objectNode);
                            jn = objectNode;
                        }
                    }
                }
            }
            else {
                System.err.println("Error Not matched");
            }
        }
    }

    public ObjectNode getJsonValue() {
        return this.on;
    }
}
