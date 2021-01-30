package smartdoc.dashboard.projector;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import smartdoc.dashboard.util.PathValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


/**
 * The class SAXReaderXmlProjector {@link JsonProjector}
 * <p>
 * <p>
 * JacksonXml parse flatten list data to xml string
 *
 * @author Maple
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
        OutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(os, new XmlLinkedHashMap(jsonProjector.projectToMap()));

            return os.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
