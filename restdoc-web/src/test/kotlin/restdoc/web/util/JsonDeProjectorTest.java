package restdoc.web.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import restdoc.web.model.doc.http.BodyFieldDescriptor;
import restdoc.web.projector.JsonDeProjector;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonDeProjectorTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Test
    public void testDeProject() throws IOException {

        JsonNode jsonNode = mapper.readValue(
                new File("E:\\jw\\rest-doc\\restdoc-web\\src\\test\\kotlin\\restdoc\\web\\util\\deproject\\sample1.json"),
                JsonNode.class);

        List<BodyFieldDescriptor> descriptors = new JsonDeProjector(jsonNode).deProject();

        System.err.println(mapper.writeValueAsString(descriptors));
    }
}
