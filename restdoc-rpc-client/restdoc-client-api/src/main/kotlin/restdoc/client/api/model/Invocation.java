package restdoc.client.api.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Invocation
 *
 * @author Maple
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public interface Invocation {
}
