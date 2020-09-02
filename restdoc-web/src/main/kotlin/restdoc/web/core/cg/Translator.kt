package restdoc.core.cg

/**
 * Source code version
 */
class SourceVersion(val version: String, val rank: Int) : Comparable<SourceVersion> {
    override fun compareTo(other: SourceVersion): Int {
        return this.rank - other.rank
    }
}

/**
 * @sample SourceVersion
 */
enum class ProgrammingType {

    /**
     * Java
     */
    JAVA,

    /**
     * Go
     */
    GO,
    PYTHON,


    JAVASCRIPT,
    KOTLIN,
    RUST,
    C,
    C_PLUS,
    SWIFT,
    OBJECT_C
}


/**
 * @sample kotlin.stackTrace
 * @since 1.0
 */
interface Translator {
    /**
     *
     */
    fun sourceTemplate(): String

    /**
     *
     */
    fun programmingType(): ProgrammingType

    /**
     * Convert And Encode Given Source Code
     *
     */
    fun encode(): String

    /**
     *
     */
    fun sourceVersion(): Array<SourceVersion>

    /**
     *
     */
    fun explain(): String
}


/**
 * Translator json to source code
 * @since 1.0
 */
class JavaTranslator : Translator {

    override fun sourceTemplate(): String {
        return ""
    }

    override fun programmingType(): ProgrammingType {
        TODO("Not yet implemented")
    }

    override fun sourceVersion(): Array<SourceVersion> {
        TODO("Not yet implemented")
    }

    override fun explain(): String {
        TODO("Not yet implemented")
    }

    override fun encode(): String {
        return ""
    }
}