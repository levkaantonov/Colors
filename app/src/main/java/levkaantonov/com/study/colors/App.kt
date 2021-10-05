package levkaantonov.com.study.colors

import android.app.Application
import levkaantonov.com.study.colors.model.colors.InMemoryColorsRepository

class App : Application() {

    val models = listOf<Any>(
        InMemoryColorsRepository()
    )
}