package restdoc.web.project;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The class XmlLinkedHashMap
 *
 * @see JacksonXmlRootElement#localName()
 * @see JacksonXmlRootElement#namespace()
 */
@JacksonXmlRootElement(localName = "root")
public class XmlLinkedHashMap  extends LinkedHashMap<String, Object> {
    public XmlLinkedHashMap() {super();}
    public XmlLinkedHashMap(Map<? extends String, ?> m) {
        super(m);
    }
}