package restdoc.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import java.io.File


class JacksonTest {

    private val mapper: ObjectMapper = ObjectMapper()

    @Test
    fun testJsonNodeType(){
        val tree = mapper.readTree(File("C:\\Users\\mxf\\IdeaProjects\\rest-doc\\src\\test\\kotlin\\sample.json"))

        for (element in tree.elements()) {

            print(element)
            println(element.isTextual)

        }
    }

    @Test
    fun testJsonPath(){
        val tree = mapper.readTree(File("C:\\Users\\mxf\\IdeaProjects\\rest-doc\\src\\test\\kotlin\\sample.json"))

        for (field in tree.fields()) {
            println("${field.key}:${field.value} ")
        }
    }

}