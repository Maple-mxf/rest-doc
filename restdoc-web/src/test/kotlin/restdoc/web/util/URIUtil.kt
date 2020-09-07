package restdoc.web.util

import org.junit.Test
import java.net.URI

class URIUtil {

    @Test
    fun testParseURIVar() {
        val uri = URI("https://localhost:8090/a/b")

        println(uri.host)
        println(uri.rawPath)
    }

}