package restdoc.web.util

import org.junit.Test
import java.net.URI

class URITest {

    @Test
    fun testGetAddress(){
        val uri = URI("https://localhost:8090/a/b/c")
        val url = uri.toURL()
    }
}