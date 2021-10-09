package levkaantonov.com.study.colors

import android.app.Application
import foundation.BaseApplication
import foundation.model.coroutines.IoDispatcher
import foundation.model.coroutines.WorkerDispatcher
import foundation.model.dispatchers.MainThreadDispatcher
import kotlinx.coroutines.Dispatchers
import levkaantonov.com.study.colors.model.colors.InMemoryColorsRepository

class App : Application(), BaseApplication {
    private val ioDispatcher = IoDispatcher(Dispatchers.IO)
    private val workerDispatcher = WorkerDispatcher(Dispatchers.Default)

    override val singletonScopeDependencies = listOf(
        InMemoryColorsRepository(ioDispatcher)
    )
}