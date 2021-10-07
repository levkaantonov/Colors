package foundation.navigator

import android.os.Bundle
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import foundation.ARG_SCREEN
import foundation.utils.Event
import foundation.views.BaseFragment
import foundation.views.BaseScreen
import foundation.views.HasScreenTitle

class StackFragmentNavigator(
    private val activity: AppCompatActivity,
    @IdRes private val containerId: Int,
    private val defaultTitle: String,
    private val animations: Animations,
    private val initialScreenCreator: () -> BaseScreen
) : Navigator {

    private var result: Event<Any>? = null

    private val fragmentCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            notifyScreenUpdates()
            publishResults(f)
        }
    }

    private fun publishResults(f: Fragment) {
        val result = result?.getValue() ?: return
        if (f is BaseFragment) {
            f.viewModel.onResult(result)
        }
    }

    override fun launch(screen: BaseScreen) {
        launchFragment(screen)
    }

    override fun goBack(result: Any?) {
        if (result != null) {
            this.result = Event(result)
        }
        activity.onBackPressed()
    }

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            launchFragment(
                screen = initialScreenCreator(),
                addToBackStack = false
            )
        }
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallbacks, false)
    }

    fun onDestroy() {
        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallbacks)
    }

    fun onBackPressed() {
        val f: Fragment? = getCurrentFragment()
        if(f is BaseFragment){
            f.viewModel.onBackPressed()
        }
    }

    private fun getCurrentFragment(): Fragment? {
        return activity.supportFragmentManager.findFragmentById(containerId)
    }

    fun notifyScreenUpdates() {
        val f = getCurrentFragment()

        if (activity.supportFragmentManager.backStackEntryCount > 0) {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        if (f is HasScreenTitle && f.getScreenTitle() != null) {
            activity.supportActionBar?.title = f.getScreenTitle()
        } else {
            activity.supportActionBar?.title = defaultTitle
        }

    }

    private fun launchFragment(screen: BaseScreen, addToBackStack: Boolean = true) {
        val fragment = screen.javaClass.enclosingClass.newInstance() as Fragment
        fragment.arguments = bundleOf(ARG_SCREEN to screen)

        val transaction = activity.supportFragmentManager.beginTransaction()
        if (addToBackStack) transaction.addToBackStack(null)
        transaction
            .setCustomAnimations(
                animations.enter_anim,
                animations.exit_anim,
                animations.pop_enter_anim,
                animations.pop_exit_anim
            )
            .replace(containerId, fragment)
            .commit()
    }

    class Animations(
        @AnimRes val enter_anim: Int,
        @AnimRes val exit_anim: Int,
        @AnimRes val pop_enter_anim: Int,
        @AnimRes val pop_exit_anim: Int
    )
}