package restdoc.web.util.dp;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The class XmlLinkedHashMap
 *
 * @see JacksonXmlRootElement#localName()
 * @see JacksonXmlRootElement#namespace()
 * @param <String>
 * @param <Object>
 */
@JacksonXmlRootElement(localName = "root")
class XmlLinkedHashMap<String, Object> extends LinkedHashMap<String, Object> {
    XmlLinkedHashMap(Map<? extends String, ? extends Object> m) {
        super(m);
    }
}