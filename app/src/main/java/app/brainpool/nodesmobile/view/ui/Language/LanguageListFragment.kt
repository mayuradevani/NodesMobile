package app.brainpool.nodesmobile.view.ui.Language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.brainpool.nodesmobile.databinding.FragmentLanguageListBinding
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.visible
import app.brainpool.nodesmobile.view.adapter.LanguageAdapter
import app.brainpool.nodesmobile.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LanguageListFragment : Fragment() {
    lateinit var binding: FragmentLanguageListBinding
    private val languageAdapter by lazy { LanguageAdapter() }
    private val viewModel by viewModels<LanguageViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.languageRv.adapter = languageAdapter
        viewModel.queryLanguageList()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.launguageCodeList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.languageRv.gone()
                    binding.languageFetchProgress.gone()
                }
                is ViewState.Success -> {
                    if (response.value?.data?.getAllLanguageCodes?.size == 0) {
                        binding.languageRv.gone()
                        binding.languageFetchProgress.gone()
                        binding.languageEmptyText.visible()
                    } else {
                        binding.languageRv.visible()
                        binding.languageEmptyText.gone()
                        val results = response.value?.data?.getAllLanguageCodes
                        languageAdapter.submitList(results)
                        binding.languageFetchProgress.gone()
                    }
                }
                is ViewState.Error->{
                    languageAdapter.submitList(emptyList())
                    binding.languageRv.gone()
                    binding.languageFetchProgress.gone()
                    binding.languageEmptyText.visible()
                }
            }
        }
    }
}