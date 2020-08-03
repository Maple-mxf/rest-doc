package restdoc.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import restdoc.model.BodyFieldDescriptor

infix fun Any?.ifNull(block: () -> Unit) {
    if (this == null) block()
}


