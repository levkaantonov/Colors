package foundation

import androidx.lifecycle.ViewModel
import foundation.navigator.IntermediateNavigator
import foundation.navigator.Navigator
import foundation.uiactions.UiActions

const val ARG_SCREEN = "ARG_SCREEN"

class ActivityScopeViewModel(
    val uiActions: UiActions,
    val navigator: IntermediateNavigator
) : ViewModel(), Navigator by navigator, UiActions by uiActions {

    override fun onCleared() {
        super.onCleared()
        navigator.clear()
    }
}