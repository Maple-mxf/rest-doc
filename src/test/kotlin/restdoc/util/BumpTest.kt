package restdoc.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import restdoc.web.Bump
import java.io.File

class BumpTest {
    private val mapper: ObjectMapper = ObjectMapper()


    @Test
    fun testBumpJson() {
        val bump = Bump(File("E:\\jw\\rest-doc\\src\\test\\kotlin\\request.json").readText())

        println(mapper.writeValueAsString(bump.bump()))
    }

}