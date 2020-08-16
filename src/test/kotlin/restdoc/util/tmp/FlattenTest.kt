package restdoc.util.tmp

import org.apache.catalina.mapper.Mapper
import org.junit.Test
import restdoc.model.BodyFieldDescriptor
import restdoc.model.FieldType
import restdoc.util.tmp.Flattern2
import restdoc.web.Flatten


public class FlattenTest {

    val flatten: Flatten = Flatten()

    val flatten2: Flattern2 = Flattern2()

    // []/[]
    // []/[]
    @Test
    fun flattenArray() {

        val fields: List<BodyFieldDescriptor> = mutableListOf(
                BodyFieldDescriptor(path = "[]users[].settings", value = null, description = "", type = FieldType.ARRAY, optional = false, defaultValue = ""),
                BodyFieldDescriptor(path = "[]users[].settings.name", value = null, description = "", type = FieldType.ARRAY, optional = false, defaultValue = "")
        )

        val flattenNodeToTree = flatten.flattenNodeToTree(fields)

        // "^(\\[\\])+[a-zA-Z]+[0-9](\\[\\])?+$"
//        println(Mapper)
    }

    @Test
    fun flatternArray2(){
        val fields: List<BodyFieldDescriptor> = mutableListOf(
                BodyFieldDescriptor(path = "[]users[].settings", value = null, description = "", type = FieldType.ARRAY, optional = false, defaultValue = ""),
                BodyFieldDescriptor(path = "[]users[].settings.name", value = null, description = "", type = FieldType.ARRAY, optional = false, defaultValue = "")
        )

        val flattenNodeToTree = flatten2.flattenNodeToTree(fields)

        println(flattenNodeToTree)
    }

}