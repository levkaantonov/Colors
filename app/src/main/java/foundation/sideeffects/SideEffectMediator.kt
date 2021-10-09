package foundation.sideeffects

import foundation.model.dispatchers.Dispatcher
import foundation.model.dispatchers.MainThreadDispatcher
import foundation.utils.ResourceActions

open class SideEffectMediator<Implementation>(
    dispatcher: Dispatcher = MainThreadDispatcher()
) {

    protected val target = ResourceActions<Implementation>(dispatcher)

    fun setTarget(target: Implementation?) {
        this.target.resource = target
    }

    fun clear() {
        target.clear()
    }

}