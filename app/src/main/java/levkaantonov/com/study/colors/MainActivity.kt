package levkaantonov.com.study.colors

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import foundation.ActivityScopeViewModel
import foundation.navigator.IntermediateNavigator
import foundation.navigator.StackFragmentNavigator
import foundation.navigator.StackFragmentNavigator.*
import foundation.uiactions.AndroidUiActions
import foundation.utils.viewModelCreator
import foundation.views.FragmentsHolder
import levkaantonov.com.study.colors.databinding.ActivityMainBinding
import levkaantonov.com.study.colors.views.currentcolor.CurrentColorFragment

class MainActivity : AppCompatActivity(), FragmentsHolder {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigator: StackFragmentNavigator

    private val activityViewModel by viewModelCreator<ActivityScopeViewModel> {
        ActivityScopeViewModel(
            uiActions = AndroidUiActions(applicationContext),
            navigator = IntermediateNavigator()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigator = StackFragmentNavigator(
            activity = this,
            defaultTitle = getString(R.string.app_name),
            animations = Animations(
                R.anim.enter,
                R.anim.exit,
                R.anim.pop_enter,
                R.anim.pop_exit
            ),
            containerId = binding.fragmentContainerView.id
        ) { CurrentColorFragment.Screen() }
        navigator.onCreate(savedInstanceState)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        activityViewModel.navigator.setTarget(navigator)
    }

    override fun onPause() {
        super.onPause()
        activityViewModel.navigator.setTarget(null)
    }

    override fun onDestroy() {
        navigator.onDestroy()
        super.onDestroy()
    }

    override fun notifyScreenUpdates() {
        navigator.notifyScreenUpdates()
    }

    override fun getActivityScopeViewModel(): ActivityScopeViewModel {
        return activityViewModel
    }

    override fun onBackPressed() {
        navigator.onBackPressed()
        super.onBackPressed()
    }
}