package restdoc.util.tmp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.util.CollectionUtils;
import restdoc.model.BodyFieldDescriptor;

import java.util.List;

public class Flattern2 {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode flattenNodeToTree(List<BodyFieldDescriptor> bodyFieldDescriptorList) {

        if (CollectionUtils.isEmpty(bodyFieldDescriptorList)) {
            return objectMapper.createObjectNode();
        }

        TreeNode root = new TreeNode(null, null);
        TreeNode pointer;
        for (BodyFieldDescriptor descriptor : bodyFieldDescriptorList) {
            pointer = root.addChild(descriptor.getPath());
            pointer.setType(descriptor.getType());
            if (pointer != null) {
                pointer.setValue(descriptor.getValue());
            }
        }
        return toJsonNode(root.getChildren());
    }

    public static JsonNode toJsonNode(List<TreeNode> treeNodeList) {
        if (CollectionUtils.isEmpty(treeNodeList)) {
            return null;
        }
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (TreeNode tn : treeNodeList) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (tn.getName() != null) {
                objectNode.put("path", tn.getName());
            }
            if (!tn.isEmpty()) {
                objectNode.set("children", toJsonNode(tn.getChildren()));
            }
            arrayNode.add(objectNode);
        }
        if (arrayNode.size() == 1) {
            return arrayNode.get(0);
        }
        return arrayNode;
    }

}
