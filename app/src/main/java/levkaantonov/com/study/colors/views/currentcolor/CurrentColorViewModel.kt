package levkaantonov.com.study.colors.views.currentcolor

import foundation.model.PendingResult
import foundation.model.SuccessResult
import foundation.model.takeSuccess
import foundation.model.tasks.dispatchers.Dispatcher
import foundation.navigator.Navigator
import foundation.uiactions.UiActions
import foundation.views.BaseViewModel
import foundation.views.LiveResult
import foundation.views.MutableLiveResult
import levkaantonov.com.study.colors.R
import levkaantonov.com.study.colors.model.colors.ColorListener
import levkaantonov.com.study.colors.model.colors.ColorsRepository
import levkaantonov.com.study.colors.model.colors.NamedColor
import levkaantonov.com.study.colors.views.changecolor.ChangeColorFragment

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    dispatcher: Dispatcher
) : BaseViewModel(dispatcher) {

    private val _currentColor = MutableLiveResult<NamedColor>(PendingResult())
    val currentColor: LiveResult<NamedColor> = _currentColor

    private val colorListener: ColorListener = {
        _currentColor.postValue(SuccessResult(it))
    }

    init {
        colorsRepository.addListener(colorListener)
        load()
    }

    private fun load() {
        colorsRepository.getCurrentColor().into(_currentColor)
    }

    override fun onCleared() {
        super.onCleared()
        colorsRepository.removeListener(colorListener)
    }

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = uiActions.getString(R.string.changed_color, result.name)
            uiActions.toast(message)
        }
    }

    fun changeColor() {
        val currentColor = currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    fun tryAgain() {
        load()
    }

}