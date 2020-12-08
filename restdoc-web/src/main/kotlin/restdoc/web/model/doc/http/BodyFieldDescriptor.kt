package restdoc.web.model.doc.http

import restdoc.web.util.FieldType

data class BodyFieldDescriptor(
        var path: String,
        var value: Any?,
        var description: String?,
        val type: FieldType = FieldType.OBJECT,
        val optional: Boolean = false,
        val defaultValue: Any? = null
) {

    override fun equals(other: Any?): Boolean {
        if (other is BodyFieldDescriptor) {
            return this.path.equals(other.path)
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}