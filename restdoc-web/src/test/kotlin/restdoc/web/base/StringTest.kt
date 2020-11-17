package restdoc.web.base

import org.junit.Test

class StringTest {

    @Test
    fun testReplace(){
        val result = "a[1].b[2]".replace(Regex("\\[\\d+\\]"), "[]")
        print(result)
    }
}