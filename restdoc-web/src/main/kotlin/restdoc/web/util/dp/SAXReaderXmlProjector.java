package restdoc.web.util.dp;

import org.dom4j.Element;
import org.dom4j.tree.BaseElement;
import restdoc.web.util.Node;
import restdoc.web.util.PathValue;

import java.util.List;

/**
 * The class SAXReaderXmlProjector {@link JsonProjector}
 * <p>
 * <p>
 * SaxReader parse flatten list data to xml string
 */
public class SAXReaderXmlProjector extends BaseProjector<String> {

    public SAXReaderXmlProjector(List<PathValue> pathValues) {

        //
        List<PathValue> pathValueList = BaseProjector.resolve(pathValues);

        //
        this.buildForTreeNode(pathValueList);
    }

    @Override
    public String project() {
        return null;
    }

    protected Element build4XmlNode(List<PathValue> pathValueList) {

        List<Node> children = this.nodeTree.getChildren();

        final Element rootEle = new BaseElement("root");

        return rootEle;
    }

    private class ChildXmlBuilder {

        private final Element rootEle = new BaseElement("root");

        private ChildXmlBuilder(Node parentNode) {
            this.build(parentNode, rootEle);
        }

        private void build(Node pn, Element element) {
            List<Node> children = pn.getChildren();
            String[] fields = pn.getPath().split("\\.");
            String lastField = fields[fields.length - 1];

//            if (element.)
        }
    }


}
