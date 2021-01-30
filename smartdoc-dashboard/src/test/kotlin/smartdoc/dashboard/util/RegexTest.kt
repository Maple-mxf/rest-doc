package smartdoc.dashboard.util

import org.junit.Test

class RegexTest {

    @Test
    fun testParseURL1() {

        /*val matches = "/site/abc".matches()
        println(matches)*/

        val regex = Regex("^([/][a-zA-Z0-9])+[/]?$")
        val matchResult = regex.find("/a/b/")

        println(matchResult?.groupValues)

        val matches = regex.matches("/a/b/")
        println(matches)

        println("/a/b/".matches(regex))
    }


    @Test
    fun testSplit() {
        val url = "localhost:9090/site/a"
        val arr = url.split(delimiters = *arrayOf("/"))
        val ret = if (arr.size == 1) arr[0]
        else arr.subList(1, arr.size).joinToString(separator = "/")

        println(arr.size)

        println(ret)
    }

}