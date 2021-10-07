package levkaantonov.com.study.colors

import android.app.Application
import foundation.BaseApplication
import foundation.model.Repository
import foundation.model.tasks.SimpleTasksFactory
import levkaantonov.com.study.colors.model.colors.InMemoryColorsRepository

class App : Application(), BaseApplication {

    private val tasksFactory = SimpleTasksFactory()

    override val repositories = listOf(
        InMemoryColorsRepository(tasksFactory),
        tasksFactory
    )
}