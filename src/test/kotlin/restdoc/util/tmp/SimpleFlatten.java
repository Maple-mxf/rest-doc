package restdoc.util.tmp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import restdoc.model.BodyFieldDescriptor;

import java.util.List;

public class SimpleFlatten {

    ObjectMapper mapper = new ObjectMapper();

    List<BodyFieldDescriptor> descriptors;

    JsonNode finalJsonNode;

    public SimpleFlatten(List<BodyFieldDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    public void flatten() {
        boolean rootIsArray = descriptors.stream().anyMatch(descriptor -> descriptor.getPath().matches("^(\\[\\])+(.*)$"));
        finalJsonNode = rootIsArray ? mapper.createArrayNode() : mapper.createObjectNode();
        if (rootIsArray)
            flattenArray();
        else
            flattenObject();
    }

    public void flattenArray() {

        ArrayNode arrayNode = (ArrayNode) this.finalJsonNode;

        for (BodyFieldDescriptor descriptor : this.descriptors) {
            String[] splitArray = descriptor.getPath().split("\\.");

            for (String path : splitArray) {
                path.replaceAll("\\[\\]", "");
            }
        }
    }

    public void flattenObject() {

    }
}
