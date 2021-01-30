package smartdoc.dashboard.model.doc.http

import org.springframework.web.bind.annotation.MatrixVariable

/**
 * @see MatrixVariable
 *
 * @since 2.0.RELEASE
 * @author Maple
 */
class MatrixVariableDescriptor {

    /**
     *@see org.springframework.web.bind.annotation.MatrixVariable.name
     *@see org.springframework.web.bind.annotation.MatrixVariable.value
     */
    var field: String? = null

    /**
     * @see org.springframework.web.bind.annotation.MatrixVariable.required
     */
    var required: Boolean = true

    /**
     * @see QueryParamDescriptor.field
     * @see org.springframework.web.bind.annotation.MatrixVariable.pathVar
     */
    var pathVar: String? = null

    /**
     * @see org.springframework.web.bind.annotation.MatrixVariable.defaultValue
     */
    var defaultValue: Any? = null
}