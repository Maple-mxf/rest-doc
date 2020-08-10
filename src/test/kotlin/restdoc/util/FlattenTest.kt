package restdoc.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import restdoc.model.BodyFieldDescriptor
import restdoc.model.FieldType
import restdoc.web.Flatten


class FlattenTest {

    val flatten: Flatten = Flatten()

    private val mapper: ObjectMapper = ObjectMapper()

    // []/[]
    // []/[]
    @Test
    fun flattenArray() {

        val fields: List<BodyFieldDescriptor> = mutableListOf(
                BodyFieldDescriptor(path = "[]users[].settings", value = null, description = "", type = FieldType.ARRAY, optional = false, defaultValue = ""),
                BodyFieldDescriptor(path = "[]users[].settings.name", value = null, description = "", type = FieldType.ARRAY, optional = false, defaultValue = "")/*,
                BodyFieldDescriptor(path = "[]personal.settings.name", value = null, description = "", type = FieldType.ARRAY, optional = false, defaultValue = "")*/
        )

        val flattenNodeToTree = flatten.flattenNodeToTree(fields)

        // "^(\\[\\])+[a-zA-Z]+[0-9](\\[\\])?+$"

        println(mapper.writeValueAsString(flattenNodeToTree))
    }

    @Test
    fun regex() {
//        Regex("^[a-zA-Z]+[0-9]+$")s

        println("users".matches(Regex("^[a-z]+[0-9]?$")))
    }

}