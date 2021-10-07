package foundation.model.tasks

import foundation.model.FinalResult

typealias TaskListener<T> = (FinalResult<T>) -> Unit

interface Task<T> {

    fun await(): T

    fun cancel()

    fun enqueue(listener: TaskListener<T>)
}