package restdoc.web.util.dp;

import restdoc.web.model.BodyFieldDescriptor;

import java.util.List;

/**
 * DeProjector
 */
public interface DeProjector {

    /**
     * @return The flatten java pojo
     */
    @Deprecated
    List<BodyFieldDescriptor> deProject();
}
