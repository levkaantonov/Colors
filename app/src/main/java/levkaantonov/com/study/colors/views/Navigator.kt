package levkaantonov.com.study.colors.views

import levkaantonov.com.study.colors.views.base.BaseScreen

interface Navigator {

    fun launch(screen: BaseScreen)

    fun goBack(result: Any? = null)

}