package foundation.model.tasks

import android.os.Handler
import android.os.Looper
import foundation.model.ErrorResult
import foundation.model.FinalResult
import foundation.model.SuccessResult

private val handler = Handler(Looper.getMainLooper())

class SimpleTasksFactory : TasksFactory {

    override fun <T> async(body: TaskBody<T>): Task<T> {
        return SimpleTask(body)
    }

    class SimpleTask<T>(
        private val body: TaskBody<T>
    ) : Task<T> {

        private var thread: Thread? = null
        private var canceled = false

        override fun await(): T {
            return body()
        }

        override fun cancel() {
            canceled = true
            thread?.interrupt()
            thread = null
        }

        override fun enqueue(listener: TaskListener<T>) {
            thread = Thread {
                try {
                    val data = body()
                    publishResult(listener, SuccessResult(data))
                } catch (e: Exception) {
                    publishResult(listener, ErrorResult(e))
                }
            }.apply { start() }
        }

        private fun publishResult(listener: TaskListener<T>, result: FinalResult<T>) {
            handler.post {
                if (canceled) return@post
                listener(result)
            }
        }

    }
}