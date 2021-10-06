package foundation.utils

typealias ResourceAction<T> = (T) -> Unit

class ResourceActions<T> {

    private val actions = mutableListOf<ResourceAction<T>>()
    var resource: T? = null
        set(value) {
            field = value
            if (value != null) {
                actions.forEach { it(value) }
                actions.clear()
            }
        }

    operator fun invoke(action: ResourceAction<T>) {
        val resource = this.resource
        if (resource == null) {
            actions += action
        } else {
            action(resource)
        }
    }

    fun clear() {
        actions.clear()
    }
}