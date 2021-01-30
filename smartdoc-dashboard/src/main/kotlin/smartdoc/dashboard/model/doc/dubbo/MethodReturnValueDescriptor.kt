package smartdoc.dashboard.model.doc.dubbo


class MethodReturnValueDescriptor {

    /**
     * return type
     *
     * example: java.lang.Void
     */
    var type: String = ""

    /**
     *
     */
    var sampleValue: Any? = null

    /**
     *
     */
    var description: String? = ""
}