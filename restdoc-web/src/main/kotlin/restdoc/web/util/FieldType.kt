package restdoc.web.util

/**
 * @sample JSON
 * @since 1.0
 */
enum class FieldType {

    STRING {
        override fun isPrimitive(): Boolean {
            return true
        }
    }
    ,
    NUMBER {
        override fun isPrimitive(): Boolean {
            return true
        }
    },
    OBJECT {
        override fun isPrimitive(): Boolean {
            return false
        }
    },
    ARRAY {
        override fun isPrimitive(): Boolean {
            return false
        }
    },
    BOOLEAN {
        override fun isPrimitive(): Boolean {
            return true
        }
    },
    MISSING {
        override fun isPrimitive(): Boolean {
            return true
        }
    };

    abstract fun isPrimitive(): Boolean
}