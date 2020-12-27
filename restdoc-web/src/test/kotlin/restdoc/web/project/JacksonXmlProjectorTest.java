package restdoc.web.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import restdoc.web.util.PathValue;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
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

    private final XmlMapper xmlMapper = new XmlMapper();

    @Test
    public void testProject2Xml2() throws XMLStreamException, IOException {

        List<Map<String, Object>> array = mapper.readValue(
                new File("E:\\jw\\rest-doc\\restdoc-web\\src\\test\\kotlin\\restdoc\\web\\util\\project\\sample1.json"),
                List.class);

        List<PathValue> pathValues = array.stream()
                .filter(t -> !"OBJECT".equals(t.get("type")) || !"ARRAY".equals(t.get("type")))
                .map(t -> new PathValue((String) t.get("path"), t.get("value")))
                .collect(Collectors.toList());

        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
        StringWriter out = new StringWriter();

        XMLStreamWriter sw;
        sw = xmlOutputFactory.createXMLStreamWriter(out);
        sw.writeStartDocument();

        xmlMapper.writeValue(sw, new JsonProjector(pathValues).project());
        sw.writeEndDocument();

        sw.close();

        System.err.println(out.toString());
    }

    @Test
    public void testProject2Xml3() throws IOException {
        List<Map<String, Object>> array = mapper.readValue(
                new File("E:\\jw\\rest-doc\\restdoc-web\\src\\test\\kotlin\\restdoc\\web\\util\\project\\sample1.json"),
                List.class);

        List<PathValue> pathValues = array.stream()
                .filter(t -> !"OBJECT".equals(t.get("type")) || !"ARRAY".equals(t.get("type")))
                .map(t -> new PathValue((String) t.get("path"), t.get("value")))
                .collect(Collectors.toList());

        System.err.println(new JacksonXmlProjector(pathValues).project());
        System.err.println(mapper.writeValueAsString(new JsonProjector(pathValues).projectToMap()));
    }
}
