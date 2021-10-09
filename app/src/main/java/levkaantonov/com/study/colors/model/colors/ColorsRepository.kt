package levkaantonov.com.study.colors.model.colors

import foundation.model.Repository

typealias ColorListener = (NamedColor) -> Unit

interface ColorsRepository : Repository {

    suspend fun getAvailableColors(): List<NamedColor>

    suspend fun getById(id: Long): NamedColor

    suspend fun setCurrentColor(color: NamedColor)

    suspend fun getCurrentColor(): NamedColor

    fun addListener(listener: ColorListener)

    fun removeListener(listener: ColorListener)
}