package foundation.views

import androidx.lifecycle.*
import foundation.model.ErrorResult
import foundation.model.Result
import foundation.model.SuccessResult
import foundation.utils.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
typealias MediatorLiveResult<T> = MediatorLiveData<Result<T>>

open class BaseViewModel : ViewModel() {

    private val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate
    protected val viewModelScope: CoroutineScope = CoroutineScope(coroutineContext)

    override fun onCleared() {
        super.onCleared()
        clearViewModelScope()
    }

    open fun onResult(result: Any) {

    }

    open fun onBackPressed(): Boolean {
        clearViewModelScope()
        return false
    }

    fun <T> into(liveResult: MutableLiveResult<T>, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                liveResult.postValue(SuccessResult(block()))
            } catch (e: Exception) {
                if (e !is CancellationException)
                    liveResult.postValue(ErrorResult(e))
            }
        }
    }

    fun <T> into(stateFlow: MutableStateFlow<Result<T>>, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                stateFlow.value = SuccessResult(block())
            } catch (e: Exception) {
                if (e !is CancellationException)
                    stateFlow.value = ErrorResult(e)
            }
        }
    }

    fun <T> SavedStateHandle.getStateFlow(key: String, initialValue: T): MutableStateFlow<T> {
        val savedStateHandle = this
        val mutableStateflow = MutableStateFlow(savedStateHandle[key] ?: initialValue)

        viewModelScope.launch {
            mutableStateflow.collect {
                savedStateHandle[key] = it
            }
        }

        viewModelScope.launch {
            savedStateHandle.getLiveData<T>(key).asFlow().collect {
                mutableStateflow.value = it
            }
        }

        return mutableStateflow
    }

    private fun clearViewModelScope() {
        viewModelScope.cancel()
    }
}