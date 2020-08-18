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

    @Test
    fun split(){
        val splitChar = "[]users[]".replace("[]", "")
        val split = "[]users[]".split(splitChar)
        println(split)
    }


    @Test
    fun parseJson(){
        val fields: List<BodyFieldDescriptor> = mutableListOf(
                BodyFieldDescriptor(path = "users[1].settings", value = null, description = "", type = FieldType.ARRAY, optional = false, defaultValue = ""),
                BodyFieldDescriptor(path = "users[1].settings.name", value = null, description = "", type = FieldType.ARRAY, optional = false, defaultValue = "")
        )
        System.err.println(JsonParser(fields).jsonValue)
    }
}