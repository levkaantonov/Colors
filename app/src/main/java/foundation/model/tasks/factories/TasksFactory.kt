package foundation.model.tasks.factories

import foundation.model.tasks.Task

typealias TaskBody<T> = () -> T

interface TasksFactory {

    fun <T> async(body: TaskBody<T>): Task<T>
}