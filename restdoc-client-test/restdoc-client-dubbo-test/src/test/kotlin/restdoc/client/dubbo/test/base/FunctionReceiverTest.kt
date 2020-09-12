package restdoc.client.dubbo.test.base

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
        val instance: ConvertStringUpper = fun(input: String): String { return  input.toUpperCase() }
    }

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
}

class InstanceFunctionByType : (String) -> String {
    override fun invoke(input: String): String {
        return input.toUpperCase()
    }
}

val FunctionByTypeInstance = InstanceFunctionByType()
