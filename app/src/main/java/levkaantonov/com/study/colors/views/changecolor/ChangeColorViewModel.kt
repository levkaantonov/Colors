package levkaantonov.com.study.colors.views.changecolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations.map
import foundation.model.FinalResult
import foundation.model.PendingResult
import foundation.model.SuccessResult
import foundation.sideeffects.navigator.Navigator
import foundation.sideeffects.resources.Resources
import foundation.sideeffects.toasts.Toasts
import foundation.views.BaseViewModel
import foundation.views.LiveResult
import foundation.views.MediatorLiveResult
import foundation.views.MutableLiveResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import levkaantonov.com.study.colors.R
import levkaantonov.com.study.colors.model.colors.ColorsRepository
import levkaantonov.com.study.colors.model.colors.NamedColor
import levkaantonov.com.study.colors.views.changecolor.ChangeColorFragment.Screen
import levkaantonov.com.study.colors.views.changecolor.ChangeColorFragment.ViewState

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle,
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
            resources.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            resources.getString(R.string.change_color_screen_simple)
        }
    }

    init {
        load()
        _viewState.addSource(_availableColors) { mergeSources() }
        _viewState.addSource(_currentColorId) { mergeSources() }
        _viewState.addSource(_saveInProgress) { mergeSources() }
    }

    private fun load() = into(_availableColors) {
        colorsRepository.getAvailableColors()
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = viewModelScope.launch {
        try {
            _saveInProgress.postValue(true)
            val currentColorId =
                _currentColorId.value ?: throw IllegalStateException("Color ID should be not null")
            val currentColor = colorsRepository.getById(currentColorId)
            colorsRepository.setCurrentColor(currentColor)
            navigator.goBack(currentColor)
        } catch (e: Exception) {
            if (e !is CancellationException)
                toasts.toast(resources.getString(R.string.error_happened))
        } finally {
            _saveInProgress.value = false
        }
    }

    private fun onSaved(result: FinalResult<NamedColor>) {

    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        load()
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