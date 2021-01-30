package smartdoc.dashboard.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import smartdoc.dashboard.projector.BaseProjector;
import smartdoc.dashboard.projector.JsonProjector;

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
    public void testResolveComplicatedArray() throws IOException {

        List<Map<String, Object>> array = mapper.readValue(
                new File("D:\\jw\\rest-doc\\restdoc-web\\src\\test\\kotlin\\restdoc\\web\\util\\project\\sample1.json"),
                List.class);

        List<PathValue> pathValues = array.stream()
                .filter(t -> !"OBJECT".equals(t.get("type")) || !"ARRAY".equals(t.get("type")))
                .map(t -> new PathValue((String) t.get("path"), t.get("value")))
                .collect(Collectors.toList());

        System.err.println(mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(new JsonProjector(pathValues).projectToMap()));
    }


    @Test
    public void testResolve() throws IOException {

        //
        List<Map<String, Object>> array = mapper.readValue(
                new File("D:\\jw\\rest-doc\\restdoc-web\\src\\test\\kotlin\\restdoc\\web\\util\\project\\sample1.json"),
                List.class);

        List<PathValue> pathValues = array.stream()
                .filter(t -> !"OBJECT".equals(t.get("type")) || !"ARRAY".equals(t.get("type")))
                .map(t -> new PathValue((String) t.get("path"), t.get("value")))
                .collect(Collectors.toList());

        System.err.println(mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(BaseProjector.resolve(pathValues)));
    }

    @Test
    public void testBuildJson() throws IOException {
        List<Map<String, Object>> array = mapper.readValue(
                new File("D:\\jw\\rest-doc\\restdoc-web\\src\\test\\kotlin\\restdoc\\web\\util\\project\\sample1.json"),
                List.class);

        List<PathValue> pathValues = array.stream()
                .filter(t -> !"OBJECT".equals(t.get("type")) || !"ARRAY".equals(t.get("type")))
                .map(t -> new PathValue((String) t.get("path"), t.get("value")))
                .collect(Collectors.toList());

        System.err.println(mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(new JsonProjector((pathValues)).projectToMap()));


    }

}
