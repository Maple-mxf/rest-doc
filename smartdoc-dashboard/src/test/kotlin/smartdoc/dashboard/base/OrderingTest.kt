package smartdoc.dashboard.base

import com.google.common.collect.Ordering
import org.junit.Test

class OrderingTest {

    @Test
    fun testOrder(){
        // from/of new Instance
        val comparator = Ordering.from<String> { t1, t2 -> t1.compareTo(t2) }
        val max = comparator.max(listOf("1", "21"))
        print(max)
    }
}