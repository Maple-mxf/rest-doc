package restdoc.web.util.dp;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import restdoc.web.util.PathValue;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;


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
     * project the pathValues to xml string
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
