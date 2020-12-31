package restdoc.web.projector;

import restdoc.web.model.doc.http.BodyFieldDescriptor;

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
