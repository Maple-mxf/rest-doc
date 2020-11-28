package restdoc.web.util.dp;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import restdoc.web.core.Status;
import restdoc.web.util.FieldType;
import restdoc.web.util.Node;
import restdoc.web.util.PathValue;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static restdoc.web.core.StandardKt.throwError;

/**
 * The class SAXReaderXmlProjector {@link JsonProjector}
 * <p>
 * <p>
 * SaxReader parse flatten list data to xml string
 */
public class JacksonXmlProjector extends BaseProjector<String> {

    private final XmlMapper mapper = new XmlMapper();

    public JacksonXmlProjector(List<PathValue> pathValues) {
        super(pathValues);
    }


    /**
     * <pre>
     *     <LinkedHashMap>
     *     <sendDetail>
     *         <content>
     *             <name>Kobe</name>
     *             <recipient>m17793873123@163.com</recipient>
     *             <callback>false</callback>
     *             <state>SUCCESS</state>
     *         </content>
     *         <totalElements>1</totalElements>
     *     </sendDetail>
     * </LinkedHashMap>
     * </pre>
     *
     *
     * @return xml string
     */
    @Override
    public String project() {
        Object treeNode = this.build4XmlNode();

        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
        StringWriter out = new StringWriter();

        XMLStreamWriter sw;
        try {
            sw = xmlOutputFactory.createXMLStreamWriter(out);
            sw.writeStartDocument();
            mapper.writeValue(sw, treeNode);
            sw.writeEndDocument();

            sw.close();

            return out.toString();
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected Object build4XmlNode() {
        List<Node> children = this.nodeTree.getChildren();
        final Map<Object, Object> root = new LinkedHashMap<>();

        for (Node child : children) {
            Map<String, Map<String, Object>> childNode = new ChildXmlBuilder(child).getSingleRoot();
            for (String key : childNode.keySet())
                root.put(key, childNode.get(key));
        }
        return root;
    }

    private class ChildXmlBuilder {

        private final Map<String, Map<String, Object>> singleRoot = new LinkedHashMap<>();

        private ChildXmlBuilder(Node parentNode) {
            this.build(parentNode, singleRoot);
        }

        public Map<String, Map<String, Object>> getSingleRoot() {
            return singleRoot;
        }

        private void build(Node pn, Object dn) {
            List<Node> children = pn.getChildren();
            String[] fields = pn.getPath().split("\\.");
            String lastField = fields[fields.length - 1];

            if (children.isEmpty()) {
                if (dn instanceof Map) {
                    ((LinkedHashMap<String, Object>) dn).put(lastField, pn.getValue());
                } else if (dn instanceof List) {
                    Matcher matcher = ARRAY_PATTERN.matcher(lastField);
                    if (matcher.find()) {
                        String field = matcher.group(1);
                        List<Integer> indexes = BaseProjector.splitIndex(lastField.substring(field.length()));
                        int lastIndex = indexes.get(indexes.size() - 1);
                        ((List) dn).add(lastIndex, pn.getValue());
                    } else {
                        throwError(Status.INTERNAL_SERVER_ERROR);
                    }
                }

            } else {
                FieldType pnType = FieldType.OBJECT;

                if (children.stream().allMatch(node -> node.getPath().matches(String.format("^%s\\[\\d*\\]$", BaseProjector.escape(pn.getPath()))))) {
                    pnType = FieldType.ARRAY;
                }

                if (dn instanceof LinkedHashMap) {
                    if (pnType == FieldType.OBJECT) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        ((LinkedHashMap) dn).put(lastField, map);
                        dn = map;
                    } else {
                        List<Object> list = new ArrayList<>();
                        ((LinkedHashMap) dn).put(lastField, list);
                        dn = list;
                    }
                } else if (dn instanceof List) {
                    Matcher matcher = ARRAY_PATTERN.matcher(lastField);
                    if (matcher.find()) {
                        String field = matcher.group(1);
                        List<Integer> indexes = BaseProjector.splitIndex(lastField.substring(field.length()));
                        int lastIndex = indexes.get(indexes.size() - 1);

                        if (pnType == FieldType.OBJECT) {
                            Map<String, Object> map = new LinkedHashMap<>();
                            ((List) dn).add(lastIndex, map);
                            dn = map;
                        } else {
                            List<Object> list = new ArrayList<>();
                            ((List) dn).add(lastIndex, list);
                            dn = list;
                        }
                    } else {
                        throwError(Status.INTERNAL_SERVER_ERROR);
                    }
                }
                for (Node child : children) {
                    build(child, dn);
                }
            }
        }
    }


}
