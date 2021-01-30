package smartdoc.dashboard.base

class Version(val major: Int, val minor: Int) : Comparable<Version> {
    override fun compareTo(other: Version): Int {
        if (this.major != other.major) {
            return this.major - other.major
        } else if (this.minor != other.minor) {
            return this.minor - other.minor
        } else return 0
    }
}



typealias function = (string: String) -> Any

// ||
interface Function<String, Object> {
    fun apply(t: String): Object
}

fun executeCallback(func: Function<String, Object>) {
    val thatString = func.apply("a")
    print(thatString)
}

//
fun executeCallback(func: function) {
    // func()
    val thatString = func.invoke("b")
    print(thatString)
}

fun main() {
    executeCallback { t -> t.toUpperCase() }
}
