import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test

class RegexTest {

    @Test
    fun test0() {
        val string = "[][]name"

        val matches = string.matches(Regex("[\\[\\]]+[a-z0-9A-Z]+$"))

        println(matches)
    }

    val map: MutableMap<String, Any> = mutableMapOf()

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

        // Build The Tree

        // 1 find all parent node
        val pns = nodes.filter { nd -> !nd.contains("/") }

        for (pn in pns) {
            buildTree(pn, nodes)
        }
        println(mapper.writeValueAsString(map))
    }

    fun buildTree(pnName: String, nodes: List<String>) {

//        val children = nodes
//                .map { it.replace("[]", "") }
//                .filter { nd ->
//                }
//
//        // Empty
//        if (children.isEmpty()) {
//            map[pnName] = "null"
//            return
//        } else {
//            map[pnName] = children
//            // Build Tree
//            for (child in children) {
//                buildTree(child, nodes)
//            }
//        }
//    }

        // 马维祥 201706100100002
    }
}