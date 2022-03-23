package app.brainpool.nodesmobile.view.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.ImeiFragmentBinding
import com.alcophony.app.ui.core.BaseFragment

class IMEIFragment : BaseFragment(R.layout.imei_fragment) {

    lateinit var binding: ImeiFragmentBinding
    private val viewModel by viewModels<AboutViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ImeiFragmentBinding.inflate(inflater)
        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        viewModel.getUserProfileLocal()
        observeLiveData()

        return binding.root
    }

    private fun observeLiveData() {
        observeViewState(viewModel.userProfile, binding.fetchProgress) { user ->
            if (user != null) {
                binding.tvIMEI.setText(user.imei)
            }
        }
    }
}