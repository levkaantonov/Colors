package foundation.views

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import foundation.model.ErrorResult
import foundation.model.PendingResult
import foundation.model.Result
import foundation.model.SuccessResult
import foundation.views.activity.ActivityDelegateHolder

abstract class BaseFragment : Fragment() {

    abstract val viewModel: BaseViewModel

    fun notifyScreenUpdates() {
        (requireActivity() as ActivityDelegateHolder).delegate.notifyScreenUpdates()
    }

    fun <T> renderResult(
        root: ViewGroup,
        result: Result<T>,
        onPending: () -> Unit,
        onError: (Exception) -> Unit,
        onSuccess: (T) -> Unit
    ) {
        root.children.forEach { it.visibility = View.GONE }
        when (result) {
            is PendingResult -> onPending()
            is ErrorResult -> onError(result.exception)
            is SuccessResult -> onSuccess(result.data)
        }
    }
}