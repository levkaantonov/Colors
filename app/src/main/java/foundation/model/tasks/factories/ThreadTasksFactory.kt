package foundation.model.tasks.factories

import foundation.model.tasks.AbstractTask
import foundation.model.tasks.SynchronizedTask
import foundation.model.tasks.Task
import foundation.model.tasks.TaskListener

class ThreadTasksFactory : TasksFactory {

    override fun <T> async(body: TaskBody<T>): Task<T> {
        return SynchronizedTask(TreadTask(body))
    }

    private class TreadTask<T>(
        private val body: TaskBody<T>
    ) : AbstractTask<T>() {

        private var thread: Thread? = null

        override fun doEnqueue(listener: TaskListener<T>) {
            thread = Thread {
                executeBody(body, listener)
            }
            thread?.start()
        }

        override fun doCancel() {
            thread?.interrupt()
        }
    }
}