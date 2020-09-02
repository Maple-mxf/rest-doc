package restdoc.web.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import restdoc.web.model.BodyFieldDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonDeProjectorTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Test
    public void testDeProject() throws IOException {

        JsonNode jsonNode = mapper.readValue(
                new File("C:\\Users\\mxf\\IdeaProjects\\rest-doc\\src\\test\\kotlin\\sample.json"),
                JsonNode.class);

        List<BodyFieldDescriptor> descriptors = new JsonDeProjector(jsonNode).deProject();

        System.err.println(mapper.writeValueAsString(descriptors));
    }
}
