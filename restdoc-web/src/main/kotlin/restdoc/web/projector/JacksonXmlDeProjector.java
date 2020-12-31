package restdoc.web.projector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import restdoc.web.core.Status;
import restdoc.web.model.doc.http.BodyFieldDescriptor;

import java.util.ArrayList;
import java.util.List;


/**
 * The class {@link JacksonXmlDeProjector}
 *
 * @see JsonDeProjector
 */
public class JacksonXmlDeProjector implements DeProjector {

    private final String xmlText;

    private final XmlMapper xm = new XmlMapper();
    private final ObjectMapper om = new ObjectMapper();

    public JacksonXmlDeProjector(String xmlText) {
        this.xmlText = xmlText;
    }

    @Override
    public List<BodyFieldDescriptor> deProject() {
        try {
            XmlLinkedHashMap map = xm.readValue(xmlText, XmlLinkedHashMap.class);
            return new JsonDeProjector(om.convertValue(map, JsonNode.class)).deProject();
        } catch (Exception e) {
            e.printStackTrace();
            Status.BAD_REQUEST.error(String.format("xml解析错误:%s", e.getMessage()));
            return new ArrayList<>();
        }
    }
}
