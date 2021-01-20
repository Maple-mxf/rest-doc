package restdoc.web.util

/**
 * @sample JSON
 * @since 1.0
 */
enum class FieldType {

    STRING {
        override fun isPrimitive(): Boolean = true
    }
    ,
    NUMBER {
        override fun isPrimitive(): Boolean = true
    },
    OBJECT {
        override fun isPrimitive(): Boolean = false
    },
    ARRAY {
        override fun isPrimitive(): Boolean = false
    },
    BOOLEAN {
        override fun isPrimitive(): Boolean = true
    },
    FILE {
        override fun isPrimitive(): Boolean = false
    },
    MISSING {
        override fun isPrimitive(): Boolean = true
    };

    abstract fun isPrimitive(): Boolean
}