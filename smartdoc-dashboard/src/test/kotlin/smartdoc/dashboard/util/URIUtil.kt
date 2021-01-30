package smartdoc.dashboard.util

import org.junit.Test
import java.net.URI

class URIUtil {

    @Test
    fun testParseURIVar() {
        val uri = URI("https://localhost:8090/a/b")

        println(uri.host)
        println(uri.rawPath)
    }

    @Test
    fun testStartWith(){

        if ((!"http://39.106.104.216/cloudwebsite/api/".startsWith("http")) && (!"http://39.106.104.216/cloudwebsite/api/".startsWith("https")))
        {
            println((!"http://39.106.104.216/cloudwebsite/api/".startsWith("http")))
            println((!"http://39.106.104.216/cloudwebsite/api/".startsWith("https")))
            println("error")
        }
    }

}