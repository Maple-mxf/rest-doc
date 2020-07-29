package restdoc

infix fun Any?.ifNull(block: () -> Unit) {
    if (this == null) block()
}