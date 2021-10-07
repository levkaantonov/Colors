package foundation.model.tasks.dispatchers

interface Dispatcher {

    fun dispatch(block: () -> Unit)
}