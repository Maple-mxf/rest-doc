package restdoc.util

import org.junit.Test
import restdoc.model.BodyFieldDescriptor
import restdoc.model.FieldType
import restdoc.web.Flatten


class FlattenTest {

    val flatten: Flatten = Flatten()

    // []/[]
    // []/[]
    @Test
    fun flattenArray() {

        val fields: List<BodyFieldDescriptor> = mutableListOf(
                BodyFieldDescriptor(path = "[]users[].settings", value = null, description = "", type = FieldType.ARRAY, optional = false, defaultValue = ""),
                BodyFieldDescriptor(path = "[]users[].settings.name", value = null, description = "", type = FieldType.ARRAY, optional = false, defaultValue = "")
        )

        val flattenNodeToTree = flatten.flattenNodeToTree(fields)
    }

}