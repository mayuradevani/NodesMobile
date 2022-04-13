package app.brainpool.nodesmobile.view.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.HelpFragmentBinding
import app.brainpool.nodesmobile.view.ui.settings.about.AboutViewModel
import com.alcophony.app.ui.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class HelpFragment : BaseFragment(R.layout.help_fragment) {

    lateinit var binding: HelpFragmentBinding
    @ExperimentalCoroutinesApi
    private val viewModel by viewModels<AboutViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HelpFragmentBinding.inflate(inflater)
        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        return binding.root
    }
}