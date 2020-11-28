package restdoc.web.util.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import restdoc.web.util.PathValue;
import restdoc.web.util.dp.JacksonXmlProjector;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JacksonXmlProjectorTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testProject2Xml() throws IOException {
        List<Map<String, Object>> array = mapper.readValue(
                new File("E:\\jw\\rest-doc\\restdoc-web\\src\\test\\kotlin\\restdoc\\web\\util\\project\\sample1.json"),
                List.class);

        List<PathValue> pathValues = array.stream()
                .filter(t -> !"OBJECT".equals(t.get("type")) || !"ARRAY".equals(t.get("type")))
                .map(t -> new PathValue((String) t.get("path"), t.get("value")))
                .collect(Collectors.toList());

        System.err.println(new JacksonXmlProjector(pathValues).project());
    }
}
