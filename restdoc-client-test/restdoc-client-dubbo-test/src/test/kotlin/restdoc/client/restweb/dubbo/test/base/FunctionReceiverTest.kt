package restdoc.client.restweb.dubbo.test.base

import org.junit.Test

typealias ConvertStringUpper = (String) -> String

class FunctionReceiverTest {
    @Test
    fun createExtensionFun() {
        val stringUpper: FunctionReceiverTest.(String) -> String = { it.toUpperCase() }
        val result = this.stringUpper("Hello Kotlin")
        print(result)
    }

    @Test
    fun instanceTypealiasFun() {
        val instance: ConvertStringUpper = { input -> input.toUpperCase() }
    }

    @Test
    fun instanceTypealiasFun1() {
        val instance: ConvertStringUpper = { it.toUpperCase() }
    }

    @Test
    fun instanceTypealiasFun2() {
        val instance: ConvertStringUpper = String::toUpperCase
    }

    @Test
    fun anonymousInstanceTypealiasFun() {
        val instance: ConvertStringUpper = fun(input: String): String { return input.toUpperCase() }
    }

    @Test
    fun anonymousInstanceTypealiasFun1() {
        val instance: ConvertStringUpper = fun(input: String): String { return input.toUpperCase() }
    }


    @Deprecated("anonymousInstanceTypealiasFun2 return keywords must be required ")
    @Test
    fun anonymousInstanceTypealiasFun2() {
        val instance: ConvertStringUpper = fun(input: String): String { return input.toUpperCase() }
    }

    @Test
    fun invokeFunction() {
        val invokeFunctionInstance: (String) -> String = { it.toUpperCase() }
        invokeFunctionInstance("Hello Kotlin")
    }

    @Test
    fun invokeFunction1() {
        val invokeFunctionInstance: (String) -> String = { it.toUpperCase() }
        invokeFunctionInstance.invoke("Hello Kotlin")
    }

    @Test
    fun transmitLambdaExpression0() {
        val collection = listOf("Hello Kotlin", "Hello Java")
        collection.map { it.toUpperCase() }
    }

    @Test
    fun transmitLambdaExpression1() {
        val collection = listOf("Hello Kotlin", "Hello Java")
        collection.map({ it.toUpperCase() })
    }

    @Test
    fun trailingLambdas() {
        val items = listOf<String>("hello", "kotlin")
        items.fold(0) { acc, e -> acc * e.length }
    }

    @Test
    fun implicitSingleParamName() {
        val lambda: (String) -> String = { it.toUpperCase() }
    }

    @Test
    fun returnValueFromLambda() {
        val items = listOf<String>("hello", "kotlin")

        // Expression1
        items.map { it.toUpperCase() }

        // Expression1
        items.map { return@map it.toUpperCase() }
    }

    @Test
    fun underscoreVar() {
        val map = mapOf("key" to "value")
        map.forEach { (_, v) ->
            print(v)
        }
    }

    data class User(val id: String, val name: String, val age: Int)

    @Test
    fun destructingComplexParam() {
        listOf<User>(User(id = "1", name = "Jack", age = 20))
                .map { (id, name, age) ->
                    "$id$name$age"
                }
    }

    @Test
    fun wordCount() {
        val item = listOf("Hello Kotlin", "Hello Java", "Hello Scala")
        val groupingBy = item.flatMap { it.split(" ") }
                .groupingBy { it }
                .eachCount()
        println(groupingBy)
    }
}

class InstanceFunctionByType : (String) -> String {
    override fun invoke(input: String): String {
        return input.toUpperCase()
    }
}

val FunctionByTypeInstance = InstanceFunctionByType()




