package foundation.sideeffects.resources

import android.content.Context
import foundation.sideeffects.SideEffectMediator

class ResourcesSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<Nothing>(), Resources {

    override fun getString(resId: Int, vararg args: Any): String {
        return appContext.getString(resId, *args)
    }

}