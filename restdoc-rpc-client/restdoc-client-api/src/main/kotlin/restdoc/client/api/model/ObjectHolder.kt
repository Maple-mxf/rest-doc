package restdoc.client.api.model

class ObjectHolder<T> {

    lateinit var className: String

    var value: T? = null

    constructor()

    constructor(className: String, value: T) {
        this.className = className
        this.value = value
    }

}