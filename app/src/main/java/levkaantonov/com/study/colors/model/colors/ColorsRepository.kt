package levkaantonov.com.study.colors.model.colors

import foundation.model.Repository
import foundation.model.tasks.Task

typealias ColorListener = (NamedColor) -> Unit

interface ColorsRepository : Repository {

    fun getAvailableColors(): Task<List<NamedColor>>

    fun getById(id: Long): Task<NamedColor>

    fun setCurrentColor(color: NamedColor) : Task<Unit>

    fun getCurrentColor() : Task<NamedColor>

    fun addListener(listener: ColorListener)

    fun removeListener(listener: ColorListener)
}