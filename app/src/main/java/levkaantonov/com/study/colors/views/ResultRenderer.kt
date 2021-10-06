package levkaantonov.com.study.colors.views

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import foundation.model.Result
import foundation.views.BaseFragment
import levkaantonov.com.study.colors.R
import levkaantonov.com.study.colors.databinding.PartResultBinding

fun <T> BaseFragment.renderSimpleResult(
    root: ViewGroup, result: Result<T>, onSuccess: (T) -> Unit
) {
    val binding = PartResultBinding.bind(root)
    renderResult(
        root = root,
        result = result,
        onPending = {
            binding.progressBar.visibility = View.VISIBLE
        },
        onError = {
            binding.errorContainer.visibility = View.VISIBLE
        },
        onSuccess = { successData ->
            root.children
                .filter {
                    it.id != binding.progressBar.id &&
                            it.id != binding.errorContainer.id
                }
                .forEach {
                    it.visibility = View.VISIBLE
                }
            onSuccess(successData)
        }
    )
}

fun BaseFragment.onTryAgain(root: View, onTryAgainPressed: () -> Unit) {
    root.findViewById<Button>(R.id.tryAgainButton)
        .setOnClickListener { onTryAgainPressed() }
}