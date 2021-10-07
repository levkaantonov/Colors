package foundation.model.tasks

import foundation.model.FinalResult
import foundation.model.tasks.dispatchers.Dispatcher

typealias TaskListener<T> = (FinalResult<T>) -> Unit

class CanceledException(
    innerException: Exception? = null
) : Exception(innerException)


interface Task<T> {

    fun await(): T

    fun cancel()

    fun enqueue(dispatcher: Dispatcher, listener: TaskListener<T>)
}