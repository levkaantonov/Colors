package levkaantonov.com.study.colors.views.base

import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.savedstate.SavedStateRegistryOwner
import levkaantonov.com.study.colors.ARG_SCREEN
import levkaantonov.com.study.colors.App
import levkaantonov.com.study.colors.MainViewModel
import java.lang.reflect.Constructor

inline fun <reified VM : ViewModel> BaseFragment.screenViewModel() = viewModels<VM> {
    val application = requireActivity().application as App
    val screen = requireArguments().getSerializable(ARG_SCREEN) as BaseScreen

    val provider = ViewModelProvider(requireActivity(), AndroidViewModelFactory(application))
    val mainViewModel = provider[MainViewModel::class.java]

    val dependencies = listOf(screen, mainViewModel) + application.models
    ViewModelFactory(dependencies, this)
}

class ViewModelFactory(
    private val dependencies: List<Any>,
    owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val constructors = modelClass.constructors
        val constructor = constructors.maxByOrNull { it.typeParameters.size }!!

        val dependenciesWithSavedState = dependencies + handle
        val arguments = findDependencies(constructor, dependenciesWithSavedState)

        return constructor.newInstance(*arguments.toTypedArray()) as T
    }

    private fun findDependencies(
        constructor: Constructor<*>,
        dependencies: List<Any>
    ): List<Any> {
        val args = mutableListOf<Any>()
        constructor.parameterTypes.forEach { parameterClass ->
            val dependency = dependencies.first {
                parameterClass.isAssignableFrom(it.javaClass)
            }
            args.add(dependency)
        }
        return args
    }
}