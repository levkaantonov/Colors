package foundation.model.tasks

import foundation.model.Repository

typealias TaskBody<T> = () -> T

interface TasksFactory : Repository {

    fun <T> async(body: TaskBody<T>): Task<T>
}