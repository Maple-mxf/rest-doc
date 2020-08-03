import org.junit.Test

class RegexTest{

    @Test
    fun test0(){
        val string = "[][]name"

        val matches = string.matches(Regex("[\\[\\]]+[a-z0-9A-Z]+$"))

        println(matches)
    }

}