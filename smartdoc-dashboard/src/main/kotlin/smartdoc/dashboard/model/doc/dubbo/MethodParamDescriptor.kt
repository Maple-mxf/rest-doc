package smartdoc.dashboard.model.doc.dubbo

class MethodParamDescriptor {

    /**
     * Method param name
     *
     * name in document is unique
     */
    lateinit var name: String

    /**
     * Method param type
     * example: restdoc.core.Status
     */
    lateinit var type: String

    /**
     * Method sample value
     *
     * example: "HelloKitty"
     */
    var sampleValue: Any? = ""

    /**
     *
     */
    var defaultValue: Any? = null

    /**
     *
     */
    var description: String? = ""

    /**
     * Is Primitive
     */
    var primitive: Boolean = true
}