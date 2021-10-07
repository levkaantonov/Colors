package levkaantonov.com.study.colors

import android.app.Application
import foundation.BaseApplication
import foundation.model.tasks.ThreadUtils
import foundation.model.tasks.dispatchers.MainThreadDispatcher
import foundation.model.tasks.factories.ExecutorServiceTasksFactory
import foundation.model.tasks.factories.HandlerThreadTasksFactory
import levkaantonov.com.study.colors.model.colors.InMemoryColorsRepository
import java.util.concurrent.Executors

class App : Application(), BaseApplication {

    private val singleThreadExecutorTasksFactory =
        ExecutorServiceTasksFactory(Executors.newSingleThreadExecutor())
    private val cachedThreadPoolExecutorTasksFactory =
        ExecutorServiceTasksFactory(Executors.newCachedThreadPool())
    private val handlerThreadTasksFactory = HandlerThreadTasksFactory()

    private val threadUtils = ThreadUtils.Default()
    private val dispatcher = MainThreadDispatcher()

    override val singletonScopeDependencies = listOf(
        cachedThreadPoolExecutorTasksFactory,
        dispatcher,
        InMemoryColorsRepository(handlerThreadTasksFactory, threadUtils)
    )
}