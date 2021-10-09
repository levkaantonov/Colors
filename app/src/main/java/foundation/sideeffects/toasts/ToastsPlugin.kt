package foundation.sideeffects.toasts

import android.content.Context
import foundation.sideeffects.SideEffectMediator
import foundation.sideeffects.SideEffectPlugin

class ToastsPlugin : SideEffectPlugin<Toasts, Nothing> {

    override val mediatorClass: Class<Toasts>
        get() = Toasts::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<Nothing> {
        return ToastsSideEffectMediator(applicationContext)
    }

}