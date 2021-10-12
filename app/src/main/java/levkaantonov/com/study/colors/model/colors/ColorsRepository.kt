package levkaantonov.com.study.colors.model.colors

import foundation.model.Repository
import kotlinx.coroutines.flow.Flow

typealias ColorListener = (NamedColor) -> Unit

interface ColorsRepository : Repository {

    suspend fun getAvailableColors(): List<NamedColor>

    suspend fun getById(id: Long): NamedColor

    fun setCurrentColor(color: NamedColor): Flow<Int>

    suspend fun getCurrentColor(): NamedColor

    fun listenCurrentColor(): Flow<NamedColor>
}