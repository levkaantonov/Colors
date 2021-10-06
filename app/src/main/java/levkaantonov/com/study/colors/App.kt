package levkaantonov.com.study.colors

import android.app.Application
import foundation.BaseApplication
import foundation.model.Repository
import levkaantonov.com.study.colors.model.colors.InMemoryColorsRepository

class App : Application(), BaseApplication {

    override val repositories = listOf<Repository>(
        InMemoryColorsRepository()
    )
}