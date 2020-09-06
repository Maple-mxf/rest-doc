package restdoc.web.util

import org.junit.Test

class URIUtil {

    @Test
    fun testParseURIVar() {
        val uriVariables = uriVariables("/projectId/{docId}")
        println(uriVariables)
    }

}