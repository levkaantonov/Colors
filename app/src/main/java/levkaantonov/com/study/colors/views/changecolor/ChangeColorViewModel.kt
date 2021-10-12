package levkaantonov.com.study.colors.views.changecolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import foundation.model.*
import foundation.sideeffects.navigator.Navigator
import foundation.sideeffects.resources.Resources
import foundation.sideeffects.toasts.Toasts
import foundation.utils.finiteShareIn
import foundation.views.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import levkaantonov.com.study.colors.R
import levkaantonov.com.study.colors.model.colors.ColorsRepository
import levkaantonov.com.study.colors.model.colors.NamedColor
import levkaantonov.com.study.colors.views.changecolor.ChangeColorFragment.Screen

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), ColorsAdapter.Listener {

    private val _availableColors =
        MutableStateFlow<Result<List<NamedColor>>>(PendingResult())
    private val _currentColorId =
        savedStateHandle.getStateFlow("currentColorId", screen.currentColorId)
    private val _instantSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)
    private val _sampledSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)

    val viewState: Flow<Result<ViewState>> = combine(
        _availableColors,
        _currentColorId,
        _instantSaveInProgress,
        _sampledSaveInProgress,
        ::mergeSources
    )

    val screenTitle: LiveData<String> = viewState
        .map { result ->
            return@map if (result is SuccessResult) {
                val currentColor = result.data.colorsList.first { it.selected }
                resources.getString(
                    R.string.change_color_screen_title,
                    currentColor.namedColor.name
                )
            } else {
                resources.getString(R.string.change_color_screen_simple)
            }
        }.asLiveData()

    init {
        load()
    }

    private fun load() = into(_availableColors) {
        colorsRepository.getAvailableColors()
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_instantSaveInProgress.value.isInProgress()) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = viewModelScope.launch {
        try {
            _instantSaveInProgress.value = PercentageProgress.START
            _sampledSaveInProgress.value = PercentageProgress.START
            val currentColorId = _currentColorId.value
            val currentColor = colorsRepository.getById(currentColorId)

            val flow = colorsRepository.setCurrentColor(currentColor)
                .finiteShareIn(this)

            val instantJob = async {
                flow.collect { percent ->
                    _instantSaveInProgress.value = PercentageProgress(percent)
                }
            }

            val sampledJob = async {
                flow.sample(200L).collect { percent ->
                    _sampledSaveInProgress.value = PercentageProgress(percent)
                }
            }

            instantJob.await()
            sampledJob.await()

            navigator.goBack(currentColor)
        } catch (e: Exception) {
            if (e !is CancellationException)
                toasts.toast(resources.getString(R.string.error_happened))
        } finally {
            _instantSaveInProgress.value = EmptyProgress
            _sampledSaveInProgress.value = EmptyProgress
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

    private fun mergeSources(
        colors: Result<List<NamedColor>>,
        currentColorId: Long,
        instantSaveInProgress: Progress,
        sampledSaveInProgress: Progress
    ): Result<ViewState> {
        return colors.map { colorsList ->
            ViewState(
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },
                showSaveButton = !instantSaveInProgress.isInProgress(),
                showCancelButton = !instantSaveInProgress.isInProgress(),
                showSaveProgressBar = instantSaveInProgress.isInProgress(),
                saveProgressPercentage = instantSaveInProgress.getPercentage(),
                saveProgressPercentageMessage = resources.getString(
                    R.string.percentage_value,
                    sampledSaveInProgress.getPercentage()
                )
            )
        }
    }
}

data class ViewState(
    val colorsList: List<NamedColorListItem>,
    val showSaveButton: Boolean,
    val showCancelButton: Boolean,
    val showSaveProgressBar: Boolean,
    val saveProgressPercentage: Int,
    val saveProgressPercentageMessage: String
)