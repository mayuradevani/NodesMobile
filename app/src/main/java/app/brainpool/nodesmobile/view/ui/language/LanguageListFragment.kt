package app.brainpool.nodesmobile.view.ui.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.LanguageListFragmentBinding
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.visible
import app.brainpool.nodesmobile.view.adapter.LanguageAdapter
import com.alcophony.app.ui.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LanguageListFragment : BaseFragment(R.layout.language_list_fragment) {
    lateinit var binding: LanguageListFragmentBinding
    private val languageAdapter by lazy { LanguageAdapter() }
    private val viewModel by viewModels<LanguageViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LanguageListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.languageRv.adapter = languageAdapter
        viewModel.queryLanguageList(requireContext())
        observeLiveData()
    }

    private fun observeLiveData() {
        observeViewState(viewModel.launguageCodeList, binding.languageFetchProgress) { response ->
            if (response != null) {
                if (response?.data == null) {
                    languageAdapter.submitList(emptyList())
                    binding.languageRv.gone()
                    binding.languageFetchProgress.gone()
                    binding.languageEmptyText.visible()
                } else {
                    if (response.data?.getAllLanguageCodes?.size == 0) {
                        binding.languageRv.gone()
                        binding.languageFetchProgress.gone()
                        binding.languageEmptyText.visible()
                    } else {
                        binding.languageRv.visible()
                        binding.languageEmptyText.gone()
                        val results = response.data?.getAllLanguageCodes
                        languageAdapter.submitList(results)
                        binding.languageFetchProgress.gone()
                    }
                }

            }
        }
    }
}