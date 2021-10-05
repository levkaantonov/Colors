package levkaantonov.com.study.colors.views.base

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    abstract val viewModel: BaseViewModel

    fun notifyScreenUpdates() {
        (requireActivity() as BaseActivity).notifyScreenUpdates()
    }
}