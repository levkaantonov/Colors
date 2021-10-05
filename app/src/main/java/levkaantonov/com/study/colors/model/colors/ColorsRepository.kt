package levkaantonov.com.study.colors.model.colors

import levkaantonov.com.study.colors.model.Repository

typealias ColorListener = (NamedColor) -> Unit

interface ColorsRepository : Repository {

    var currentColor: NamedColor

    fun getAvailableColors(): List<NamedColor>

    fun getById(id: Long): NamedColor

    fun addListener(listener: ColorListener)

    fun removeListener(listener: ColorListener)
}