package levkaantonov.com.study.colors.views.changecolor

import androidx.lifecycle.*
import androidx.lifecycle.Transformations.*
import foundation.model.ErrorResult
import foundation.model.PendingResult
import foundation.model.SuccessResult
import levkaantonov.com.study.colors.R
import levkaantonov.com.study.colors.model.colors.ColorsRepository
import levkaantonov.com.study.colors.model.colors.NamedColor
import foundation.navigator.Navigator
import foundation.uiactions.UiActions
import foundation.views.BaseViewModel
import foundation.views.LiveResult
import foundation.views.MediatorLiveResult
import foundation.views.MutableLiveResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import levkaantonov.com.study.colors.views.changecolor.ChangeColorFragment.*
import java.lang.RuntimeException

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ColorsAdapter.Listener {

    private val _saveInProgress = MutableLiveData(false)
    private val _availableColors = MutableLiveResult<List<NamedColor>>(PendingResult())
    private val _currentColorId =
        savedStateHandle.getLiveData("currentColorId", screen.currentColorId)

    private val _viewState = MediatorLiveResult<ViewState>()
    val viewState: LiveResult<ViewState> = _viewState

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = map(viewState) { result ->
        if (result is SuccessResult) {
            val currentColor = result.data.colorsList.first { it.selected }
            uiActions.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            uiActions.getString(R.string.change_color_screen_simple)
        }
    }

    init {
        viewModelScope.launch {
            delay(2000L)
            _availableColors.value = SuccessResult(colorsRepository.getAvailableColors())
        }
        _viewState.addSource(_availableColors) { mergeSources() }
        _viewState.addSource(_currentColorId) { mergeSources() }
        _viewState.addSource(_saveInProgress) { mergeSources() }
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() {
        viewModelScope.launch {
            _saveInProgress.postValue(true)
            delay(1000L)
            val currentColorId = _currentColorId.value ?: return@launch
            val currentColor = colorsRepository.getById(currentColorId)
            colorsRepository.currentColor = currentColor
            navigator.goBack(result = currentColor)
        }
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        viewModelScope.launch {
            _availableColors.postValue(PendingResult())
            delay(2000L)
            _availableColors.postValue(SuccessResult(colorsRepository.getAvailableColors()))
        }
    }

    private fun mergeSources() {
        val colors = _availableColors.value ?: return
        val currentColorId = _currentColorId.value ?: return
        val saveInProgress = _saveInProgress.value ?: return

        _viewState.value = colors.map { colorsList ->
            ViewState(
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },
                showSaveButton = !saveInProgress,
                showCancelButton = !saveInProgress,
                showSaveProgressBar = saveInProgress
            )
        }
    }
}