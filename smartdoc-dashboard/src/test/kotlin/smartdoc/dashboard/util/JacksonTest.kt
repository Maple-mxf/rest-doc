package restdoc.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import smartdoc.dashboard.model.doc.http.BodyFieldDescriptor
import java.io.File


class JacksonTest {

    private val mapper: ObjectMapper = ObjectMapper()

    @Test
    fun testJsonNodeType() {
        val tree = mapper.readTree(File("C:\\Users\\mxf\\IdeaProjects\\rest-doc\\src\\test\\kotlin\\sample.json"))

        for (element in tree.elements()) {

            print(element)
            println(element.isTextual)

        }
    }

    @Test
    fun testJsonPath() {
        val tree = mapper.readTree(File("C:\\Users\\mxf\\IdeaProjects\\rest-doc\\src\\test\\kotlin\\sample.json"))

        for (field in tree.fields()) {
            println("${field.key}:${field.value} ")
        }
        val array: List<BodyFieldDescriptor> = mutableListOf()
    }

    @Test
    fun testReadJsonFields() {
        val tree = mapper.readTree(File("C:\\Users\\mxf\\IdeaProjects\\rest-doc\\src\\test\\resources\\sample_json\\fields.json"))
        val node = tree.get("name")

        val fields = node.fieldNames()
        while (fields.hasNext()) {
            println(fields.next())
        }

        val iterator = node.fields()
        while (iterator.hasNext()) {
            println(iterator.next())
        }
    }
}