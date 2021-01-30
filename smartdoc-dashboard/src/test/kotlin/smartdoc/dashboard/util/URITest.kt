package smartdoc.dashboard.util

import org.junit.Test
import java.net.URI
import java.net.URL

class URITest {

    @Test
    fun testGetAddress(){
        val uri = URI("https://localhost:8090/a/b/c")
        val url = uri.toURL()
    }

    @Test
    fun testParseURL(){
        val url = URL("http://localhost:8090/1/1/{a}")
        println(url.path)
    }
}