package levkaantonov.com.study.colors.views.currentcolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import foundation.model.ErrorResult
import foundation.model.PendingResult
import foundation.model.SuccessResult
import levkaantonov.com.study.colors.databinding.FragmentCurrentColorBinding
import foundation.views.BaseFragment
import foundation.views.BaseScreen
import foundation.views.screenViewModel
import levkaantonov.com.study.colors.databinding.PartResultBinding
import levkaantonov.com.study.colors.views.onTryAgain
import levkaantonov.com.study.colors.views.renderSimpleResult

class CurrentColorFragment : BaseFragment() {
    class Screen : BaseScreen

    override val viewModel by screenViewModel<CurrentColorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCurrentColorBinding.inflate(inflater, container, false)
        viewModel.currentColor.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    binding.colorView.setBackgroundColor(it.value)
                }
            )
        }
        binding.changeColorButton.setOnClickListener {
            viewModel.changeColor()
        }

        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }

        return binding.root
    }
}