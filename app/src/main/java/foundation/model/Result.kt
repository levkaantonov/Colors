package foundation.model

import java.lang.IllegalStateException

typealias Mapper<T, R> = (T) -> R

sealed class Result<T> {

    fun <R> map(mapper: Mapper<T, R>? = null): Result<R> {
        return when (this) {
            is PendingResult -> PendingResult()
            is SuccessResult -> {
                if (mapper == null)
                    throw IllegalStateException("Mapper should be not null for success result")
                SuccessResult(mapper(this.data))
            }
            is ErrorResult -> ErrorResult(this.exception)
        }
    }
}

fun <T> Result<T>?.takeSuccess(): T? {
    return if (this is SuccessResult) {
        this.data
    } else {
        null
    }
}

class PendingResult<T> : Result<T>()

sealed class FinalResult<T> :Result<T>() {}

class SuccessResult<T>(val data: T) : FinalResult<T>()
class ErrorResult<T>(val exception: Exception) : FinalResult<T>()