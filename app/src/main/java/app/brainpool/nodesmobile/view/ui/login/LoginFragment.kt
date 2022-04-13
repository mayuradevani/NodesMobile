package app.brainpool.nodesmobile.view.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.LoginFragmentBinding
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.materialDialog
import com.alcophony.app.ui.core.BaseFragment
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.login_fragment) {

    @ExperimentalCoroutinesApi
    private val viewModel by viewModels<LoginViewModel>()
    lateinit var binding: LoginFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            binding = LoginFragmentBinding.inflate(inflater)
            binding.btnSubmit.setOnClickListener {
                binding.edtEmail.validEmail {
                    binding.edtEmail.error = it
                }.apply { if (!this) return@setOnClickListener }

                viewModel.login(requireContext(), binding.edtEmail.text.toString())
            }
            return binding.root
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return container
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
    }

    private fun observeLiveData() {
        observeViewState(viewModel.login, binding.fetchProgress) { response ->
            if (response != null) {
                if (response.data == null) {
                    materialDialog(response.errors?.get(0)?.message.toString(), "", getString(R.string.ok))
                    {
                        it.dismiss()
                    }
                } else {
                    findNavController().navigate(R.id.holdingFragment)
                }
                binding.fetchProgress.gone()
            }
        }
    }
}