package restdoc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import restdoc.model.BodyFieldDescriptor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Comparator.comparingInt;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.*;


public class JsonParser {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Collection<BodyFieldDescriptor> descriptors;

    final ObjectNode on = mapper.createObjectNode();

    public JsonParser(List<BodyFieldDescriptor> descriptors) {
        this.descriptors = descriptors
                .stream()
                .flatMap(descriptor -> {
                    String[] paths = descriptor.getPath().split("\\.");
                    List<String> childrenPath = new ArrayList<>();

                    Map<String, Object> values = new HashMap<>();
                    for (int i = 0; i < paths.length; i++) {
                        String[] tmpPaths = Arrays.copyOfRange(paths, 0, i);
                        String[] ps = Arrays.copyOf(tmpPaths, i + 1, String[].class);
                        ps[i] = paths[i];

                        String joinPath = String.join(".", ps);
                        childrenPath.add(joinPath);

                        if (i == paths.length - 1) values.put(joinPath, descriptor.getValue());
                    }
                    return childrenPath
                            .stream()
                            .map(t -> new BodyFieldDescriptor(
                                    t, values.get(t),
                                    descriptor.getDescription(),
                                    descriptor.getType(),
                                    descriptor.getOptional(),
                                    descriptor.getDefaultValue()));
                })
                .collect(toMap(BodyFieldDescriptor::getPath, t -> t, (t, t2) -> t2))
                .values();

        this.parse(null);
    }


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

        } else {
            return descriptors.stream()
                    .filter(descriptor ->
                            compile(
                                    String.format("^%s\\.[a-zA-Z]+[a-zA-Z0-9]*(\\[\\d+\\])*$", this.escape(parentPath))).matcher(descriptor.getPath()).find())
                    .collect(toList());
        }
    }

    final Pattern fieldNamePattern = compile("[a-zA-Z0-9_]+[a-zA-Z0-9]*");

    private String escape(String string){
       return string.replaceAll("\\.", "\\\\.")
                .replaceAll("\\[", "\\\\[")
                .replaceAll("\\]", "\\\\]");
    }

    private void buildPOJO(String path, Object value) {
        String[] childPaths = path.split("\\.");
        JsonNode jn = this.on;

        for (String ph : childPaths) {
            Matcher fieldMatcher = fieldNamePattern.matcher(ph);

            if (fieldMatcher.find()) {
                String field = fieldMatcher.group(0);
                boolean isArray = ph.endsWith("]");
                if (isArray) {
                    Matcher matcher = compile("(\\[\\d+\\])+").matcher(path);
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
                } else {
                    Pattern filterChildPattern = compile(String.format("^%s(\\.[a-zA-Z_]+)$", this.escape(path)));
                    List<BodyFieldDescriptor> children = this.descriptors.stream()
                            .filter(t -> filterChildPattern.matcher(t.getPath()).find())
                            .collect(toList());

                    ObjectNode objectNode = (ObjectNode) jn;
                    if (children.isEmpty()) {

                        objectNode.putPOJO(field, value);
                    } else {
                        objectNode.putPOJO(field, mapper.createObjectNode());
                    }
                }
            } else {
                System.err.println("Error Not matched");
            }
        }
    }

    public ObjectNode getJsonValue() {
        return this.on;
    }
}
