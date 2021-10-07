package levkaantonov.com.study.colors.views.changecolor

import androidx.lifecycle.*
import androidx.lifecycle.Transformations.*
import foundation.model.ErrorResult
import foundation.model.FinalResult
import foundation.model.PendingResult
import foundation.model.SuccessResult
import foundation.model.tasks.dispatchers.Dispatcher
import foundation.model.tasks.factories.TasksFactory
import foundation.navigator.Navigator
import foundation.uiactions.UiActions
import foundation.views.BaseViewModel
import foundation.views.LiveResult
import foundation.views.MediatorLiveResult
import foundation.views.MutableLiveResult
import levkaantonov.com.study.colors.R
import levkaantonov.com.study.colors.model.colors.ColorsRepository
import levkaantonov.com.study.colors.model.colors.NamedColor
import levkaantonov.com.study.colors.views.changecolor.ChangeColorFragment.*

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    private val tasksFactory: TasksFactory,
    savedStateHandle: SavedStateHandle,
    dispatcher: Dispatcher
) : BaseViewModel(dispatcher), ColorsAdapter.Listener {

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
        load()
        _viewState.addSource(_availableColors) { mergeSources() }
        _viewState.addSource(_currentColorId) { mergeSources() }
        _viewState.addSource(_saveInProgress) { mergeSources() }
    }

    private fun load() {
        colorsRepository.getAvailableColors().into(_availableColors)
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() {
        _saveInProgress.postValue(true)
        tasksFactory.async {
            val currentColorId =
                _currentColorId.value ?: throw IllegalStateException("Color ID should be not null")
            val currentColor = colorsRepository.getById(currentColorId).await()
            colorsRepository.setCurrentColor(currentColor).await()
            return@async currentColor
        }.safeEnqueue {
            onSaved(it)
        }
    }

    private fun onSaved(result: FinalResult<NamedColor>) {
        _saveInProgress.value = false
        when (result) {
            is ErrorResult -> uiActions.toast(uiActions.getString(R.string.error_happened))
            is SuccessResult -> navigator.goBack(result.data)
        }
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