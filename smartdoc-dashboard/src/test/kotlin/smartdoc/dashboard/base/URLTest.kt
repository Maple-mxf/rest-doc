package smartdoc.dashboard.base

import org.junit.Test
import java.net.URL

class URLTest {

    @Test
    fun testGetQueryParam(){
        val url = URL("http://localhost:80/a?a=b&d=")
        val query = url.query
        println(query)
    }
}