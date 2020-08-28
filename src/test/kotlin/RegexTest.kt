import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test

class RegexTest {

    @Test
    fun test0() {
        val string = "[][]name"
        val matches = string.matches(Regex("[\\[\\]]+[a-z0-9A-Z]+$"))
        println(matches)
    }

    val mapper: ObjectMapper = ObjectMapper()

    @Test
    fun testSplitPaths() {
        val fields = listOf("users[].setting.name")

        val nodes = fields.flatMap { path ->

            val els = path.split(".").reversed()

            els.map { el ->
                val index = els.indexOf(el)
                val subList = els.subList(index, els.size).reversed()
                subList.joinToString("/")
            }
        }

        // ["users[]/setting/name","users[]/setting","users[]"]
        println(mapper.writeValueAsString(nodes))

        // ["users[]/setting/name","users[]/setting","users[]"]
//        println(mapper.writeValueAsString(nodes))

        // Build The Tree

        // 1 find all parent node
//        val pns = nodes.filter { nd -> !nd.contains("/") }
//
//        for (pn in pns) {
//            buildTree(pn, nodes)
//        }
    }

    fun buildTree(pnName: String, nodesName: List<String>) {

        println(nodesName)

        for (nodeName in nodesName) {

            val start = pnName.replace("[", "").replace("]", "")

            // ^users(\[\])+[/][a-zA-Z]+[0-9]+(\[\])?$
            val regex = "^${start}(\\[\\])+[/][a-zA-Z]+[0-9]?(\\[\\])?$"

            if (nodeName.matches(Regex(regex))) {
                println(nodeName)
            }
        }
    }

    /**
     * [users\[\]/setting/name][\[\]]+[/][a-zA-Z]+[0-9]+[\[\]]+$
     * [users\[\]/setting][\[\]]+[/][a-zA-Z]+[0-9]+[\[\]]+$
     * [users\[\]][\[\]]+[/][a-zA-Z]+[0-9]+[\[\]]+$
     */
    @Test
    fun testMatches() {
        // [users[]/setting/name, users[]/setting, users[]]
        val field = "users[]/s1"
        val matches = field.matches(Regex("^users(\\[\\])+[/][a-zA-Z]+[0-9]+(\\[\\])?$"))

        println(matches)
    }


    @Test
    fun testGroupRegex() {
        val path = "name[1123123][1]"

        val regex = Regex("^[a-zA-Z]+[a-zA-Z0-9]*((\\[\\d+\\])*)$")
        val matchResult = regex.find(path)

        if (matchResult != null) {
            val groupValues = matchResult.groupValues

            for (groupValue in groupValues) {
                println(groupValue)
            }
        }
    }

    @Test
    fun testSplitChar() {
        val str = "[2][1]"
        val regex = Regex("^((\\[\\d+\\])+)$")
        val matchResult = regex.find(str)
        if (matchResult != null) {
            for (groupValue in matchResult.groupValues) {
                println(groupValue)
            }
        }
        val split = str.split("]")

        for (s in split) {
            println(s.replace("[","").replace("]",""))
        }
    }

    @Test
    fun testGetIndexNum(){
        val path = "name[1][2]"
        val matchResult = Regex("((\\[\\d+\\])+)").find(path)

     matchResult?.let {
         for (groupValue in it.groupValues) {
             println(groupValue)
         }
     }
    }
}