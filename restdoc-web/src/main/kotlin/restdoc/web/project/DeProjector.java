package restdoc.web.project;

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