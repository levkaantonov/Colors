package levkaantonov.com.study.colors

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import foundation.ActivityScopeViewModel
import foundation.sideeffects.SideEffectPluginsManager
import foundation.sideeffects.dialogs.DialogsPlugin
import foundation.sideeffects.intents.IntentsPlugin
import foundation.sideeffects.navigator.NavigatorPlugin
import foundation.sideeffects.navigator.StackFragmentNavigator
import foundation.sideeffects.navigator.StackFragmentNavigator.*
import foundation.sideeffects.permissions.PermissionsPlugin
import foundation.sideeffects.resources.ResourcesPlugin
import foundation.sideeffects.toasts.ToastsPlugin
import foundation.utils.viewModelCreator
import foundation.views.activity.BaseActivity
import levkaantonov.com.study.colors.databinding.ActivityMainBinding
import levkaantonov.com.study.colors.views.currentcolor.CurrentColorFragment

class MainActivity : BaseActivity() {

    override fun registerPlugins(manager: SideEffectPluginsManager) = with(manager) {
        val navigator = createNavigator()
        register(ToastsPlugin())
        register(ResourcesPlugin())
        register(NavigatorPlugin(navigator))
        register(PermissionsPlugin())
        register(DialogsPlugin())
        register(IntentsPlugin())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun createNavigator() = StackFragmentNavigator(
        containerId = R.id.fragmentContainerView,
        defaultTitle = getString(R.string.app_name),
        animations = StackFragmentNavigator.Animations(
            enterAnim = R.anim.enter,
            exitAnim = R.anim.exit,
            popEnterAnim = R.anim.pop_enter,
            popExitAnim = R.anim.pop_exit
        ),
        initialScreenCreator = { CurrentColorFragment.Screen() }
    )

}