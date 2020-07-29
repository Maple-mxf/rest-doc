package restdoc.core

enum class Version(val version: Float) {
    V1(1.0f),
    V2(2.0f),
}


@Target(AnnotationTarget.CLASS,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.FIELD,
        AnnotationTarget.LOCAL_VARIABLE,
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.CONSTRUCTOR,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.TYPE,
        AnnotationTarget.EXPRESSION,
        AnnotationTarget.FILE,
        AnnotationTarget.TYPEALIAS)

@Retention(AnnotationRetention.SOURCE)
annotation class Since(val version: Version)