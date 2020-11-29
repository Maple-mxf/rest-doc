package restdoc.web.util.dp;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import restdoc.web.util.PathValue;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * The class SAXReaderXmlProjector {@link JsonProjector}
 * <p>
 * <p>
 * SaxReader parse flatten list data to xml string
 */
public class JacksonXmlProjector extends BaseProjector<String> {

    private final XmlMapper mapper = new XmlMapper();

    private final JsonProjector jsonProjector;

    public JacksonXmlProjector(List<PathValue> pathValues) {
        this.jsonProjector = new JsonProjector(pathValues);
    }

    /**
     * @param <String>
     * @param <Object>
     */
    @JacksonXmlRootElement(localName = "root")
    private static class XmlLinkedHashMap<String, Object> extends LinkedHashMap<String, Object> {
        XmlLinkedHashMap(Map<? extends String, ? extends Object> m) {
            super(m);
        }
    }

    /**
     *
     * @return xml string
     */
    @Override
    public String project() {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
        StringWriter out = new StringWriter();
        try {
            XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(out);
            sw.writeStartDocument();
            mapper.writeValue(sw, new XmlLinkedHashMap(jsonProjector.projectToMap()));
            sw.writeEndDocument();

            sw.close();
            out.close();

            return out.toString();
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
