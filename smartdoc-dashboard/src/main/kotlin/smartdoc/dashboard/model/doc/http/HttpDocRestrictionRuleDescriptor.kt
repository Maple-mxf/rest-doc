package smartdoc.dashboard.model.doc.http


/**
 * The class HttpDocRestrictionRuleDescriptor
 */
class HttpDocRestrictionRuleDescriptor {
    var field: String? = null
    var value: Any? = null
    var symbolic: Symbolic? = null
}

/**
 * Symbolic
 */
enum class Symbolic(val operator: String) {

    /**
     * =
     */
    EQUAL("="),

    /**
     * !=
     */
    NOT_EQUAL("!="),

    /**
     * >
     */
    GREATER_THAN(">"),

    /**
     * >=
     */
    GREATER_THAN_OR_EQUAL(">="),

    /**
     * <
     */
    LESS_THAN("<"),

    /**
     * <=
     */
    LESS_THAN_OR_EQUAL("<="),
}