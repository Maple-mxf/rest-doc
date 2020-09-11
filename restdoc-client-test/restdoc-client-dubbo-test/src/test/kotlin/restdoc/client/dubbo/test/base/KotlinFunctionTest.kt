package restdoc.client.dubbo.test.base

import org.junit.Test

class KotlinFunctionTest {

    @Test
    fun lambda0() {
        val list = mutableListOf<String>()

        list.filter(fun(item) = item == "1")
                .map(fun(item) =
                        run {
                            if (item.contains("a")) item.toUpperCase()
                            "B"
                        }
                )
                .flatMap(fun(item) = item.split(","))

        val function = fun(string: String): String { return string.toUpperCase() }
        val result = function("Test")

        function("asdad")
        function.invoke("asdad")

        list.filter { return@filter it.contains("a") }.map { return@map it.toUpperCase() }
                .flatMap { return@flatMap it.split(",") }.map { it.toLowerCase() }

        val sum: Int.(Int) -> Int = { other -> plus(other) }
        val sum1 = fun Int.(other: Int): Int = this + other

        val receiver: KotlinFunctionTest.(s: Int) -> Unit = {
            println(it)
        }


        KotlinFunctionTest().receiver(10)
    }

    /**
     *
     */
    fun inner(a: (s: Int, a: Int) -> Double) {
        val result = a(1, 1)
        println(result)
    }
}