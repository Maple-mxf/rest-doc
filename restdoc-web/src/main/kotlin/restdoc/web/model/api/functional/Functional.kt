package restdoc.web.model.api.functional

interface Functional<T> {
    fun name(): String
    fun precursor(): List<T>
}