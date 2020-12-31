package restdoc.web.projector;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The class XmlLinkedHashMap
 *
 * @author Maple
 * @see JacksonXmlRootElement#localName()
 * @see JacksonXmlRootElement#namespace()
 * @since 1.0.RELEASE
 */
@JacksonXmlRootElement(localName = "root")
public class XmlLinkedHashMap extends LinkedHashMap<String, Object> {
    public XmlLinkedHashMap() {
        super();
    }

    public XmlLinkedHashMap(Map<? extends String, ?> m) {
        super(m);
    }
}