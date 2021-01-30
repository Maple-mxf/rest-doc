package smartdoc.dashboard.projector;

import smartdoc.dashboard.model.doc.http.BodyFieldDescriptor;
import java.util.List;

/**
 * DeProjector
 */
public interface DeProjector {

    /**
     * @return The flatten java pojo
     */
    List<BodyFieldDescriptor> deProject();
}
