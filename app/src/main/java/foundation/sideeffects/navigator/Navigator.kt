package foundation.sideeffects.navigator

import foundation.views.BaseScreen

interface Navigator {

    fun launch(screen: BaseScreen)

    fun goBack(result: Any? = null)

}