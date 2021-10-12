package levkaantonov.com.study.colors.views.changecolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import foundation.views.BaseFragment
import foundation.views.BaseScreen
import foundation.views.HasScreenTitle
import foundation.views.screenViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import levkaantonov.com.study.colors.R
import levkaantonov.com.study.colors.databinding.FragmentChangeColorBinding
import levkaantonov.com.study.colors.views.collectFlow
import levkaantonov.com.study.colors.views.onTryAgain
import levkaantonov.com.study.colors.views.renderSimpleResult

class ChangeColorFragment : BaseFragment(), HasScreenTitle {

    class Screen(
        val currentColorId: Long
    ) : BaseScreen

    override val viewModel by screenViewModel<ChangeColorViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChangeColorBinding.inflate(inflater, container, false)

        val adapter = ColorsAdapter(viewModel)
        setupLayoutManager(binding, adapter)

        binding.saveButton.setOnClickListener { viewModel.onSavePressed() }
        binding.cancelButton.setOnClickListener { viewModel.onCancelPressed() }


        collectFlow(viewModel.viewState) { result ->
            renderSimpleResult(binding.root, result) { viewState ->
                adapter.items = viewState.colorsList
                binding.saveButton.visibility =
                    if (viewState.showSaveButton) View.VISIBLE else View.INVISIBLE
                binding.cancelButton.visibility =
                    if (viewState.showCancelButton) View.VISIBLE else View.INVISIBLE

                binding.saveProgressGroup.visibility =
                    if (viewState.showSaveProgressBar) View.VISIBLE else View.GONE
                binding.saveProgressBar.progress = viewState.saveProgressPercentage
                binding.savingPercentageTextView.text = viewState.saveProgressPercentageMessage
            }
        }

        viewModel.screenTitle.observe(viewLifecycleOwner)
        {
            notifyScreenUpdates()
        }

        onTryAgain(binding.root)
        {
            viewModel.tryAgain()
        }

        return binding.root
    }

    private fun setupLayoutManager(binding: FragmentChangeColorBinding, adapter: ColorsAdapter) {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = binding.root.width
                val itemWidth = resources.getDimensionPixelSize(R.dimen.item_width)
                val columns = width / itemWidth
                binding.colorsRecyclerView.adapter = adapter
                binding.colorsRecyclerView.layoutManager =
                    GridLayoutManager(requireContext(), columns)
            }
        })
    }
}