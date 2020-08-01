package restdoc.util

infix fun Any?.ifNull(block: () -> Unit) {
    if (this == null) block()
}