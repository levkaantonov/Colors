package foundation.sideeffects.toasts

import android.content.Context
import android.widget.Toast
import foundation.model.dispatchers.MainThreadDispatcher
import foundation.sideeffects.SideEffectMediator

class ToastsSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<Nothing>(), Toasts {

    private val dispatcher = MainThreadDispatcher()

    override fun toast(message: String) {
        dispatcher.dispatch {
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
        }
    }

}