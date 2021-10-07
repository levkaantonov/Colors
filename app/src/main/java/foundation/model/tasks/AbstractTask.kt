package foundation.model.tasks

import foundation.model.ErrorResult
import foundation.model.FinalResult
import foundation.model.SuccessResult
import foundation.model.tasks.dispatchers.Dispatcher
import foundation.model.tasks.factories.TaskBody
import foundation.utils.delegates.Await

abstract class AbstractTask<T> : Task<T> {

    private var finalResult by Await<FinalResult<T>>()

    final override fun await(): T {
        val wrappedListener: TaskListener<T> = {
            finalResult = it
        }
        doEnqueue(wrappedListener)
        try {
            when (val result = finalResult) {
                is ErrorResult -> throw result.exception
                is SuccessResult -> return result.data
            }
        } catch (e: Exception) {
            if (e is InterruptedException) {
                cancel()
                throw CanceledException(e)
            } else {
                throw e
            }
        }
    }

    final override fun enqueue(dispatcher: Dispatcher, listener: TaskListener<T>) {
        val wrappedListener: TaskListener<T> = {
            finalResult = it
            dispatcher.dispatch {
                listener(finalResult)
            }
        }
        doEnqueue(wrappedListener)
    }

    final override fun cancel() {
        finalResult = ErrorResult(CanceledException())
        doCancel()
    }

    fun executeBody(taskBody: TaskBody<T>, listener: TaskListener<T>) {
        try {
            val data = taskBody()
            listener(SuccessResult(data))
        } catch (e: Exception) {
            listener(ErrorResult(e))
        }
    }

    abstract fun doEnqueue(listener: TaskListener<T>)

    abstract fun doCancel()
}