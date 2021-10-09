package foundation.sideeffects.intents

import android.content.Context
import foundation.sideeffects.SideEffectMediator
import foundation.sideeffects.SideEffectPlugin

class IntentsPlugin : SideEffectPlugin<Intents, Nothing> {

    override val mediatorClass: Class<Intents>
        get() = Intents::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<Nothing> {
        return IntentsSideEffectMediator(applicationContext)
    }

}