package restdoc.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>说明</p>
 *
 * <p>作用</p>
 *
 * <ul>
 *   <li>作用1</li>
 * </ul>
 *
 * @since 2.0
 */
public class JsonProjectorTest {

    private ObjectMapper mapper = new ObjectMapper();


    @Test
    public void testResolve() throws JsonProcessingException {
        ObjectNode jsonTree = new JsonProjector(Lists.newArrayList(
                new PathValue("users[1][1].name", "value"),
                new PathValue("users[1][1].age", "value")
        )).project();

        System.err.println(jsonTree);
    }

    @Test
    public void testResolveComplicated() throws IOException {
        List<Map<String,Object>> array = mapper.readValue(new File("D:\\jw\\rest-doc\\restdoc-web\\src\\test\\kotlin\\restdoc\\web\\util\\jsonProjector.sample1.json"),
                List.class);

        List<PathValue> pathValues = array.stream()
                .map(t -> new PathValue((String) t.get("path"), t.get("value")))
                .collect(Collectors.toList());

        System.err.println(mapper.writeValueAsString(new JsonProjector(pathValues).projectToMap()));
    }

}
