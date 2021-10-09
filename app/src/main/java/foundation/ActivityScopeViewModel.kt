package foundation

import androidx.lifecycle.ViewModel
import foundation.sideeffects.SideEffectMediator
import foundation.sideeffects.SideEffectMediatorsHolder

const val ARG_SCREEN = "ARG_SCREEN"

class ActivityScopeViewModel : ViewModel() {

    internal val sideEffectMediatorsHolder = SideEffectMediatorsHolder()

    val sideEffectMediators: List<SideEffectMediator<*>>
        get() = sideEffectMediatorsHolder.mediators

    override fun onCleared() {
        super.onCleared()
        sideEffectMediatorsHolder.clear()
    }
}