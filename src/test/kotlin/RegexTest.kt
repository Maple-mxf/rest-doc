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
}