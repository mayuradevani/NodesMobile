package app.brainpool.nodesmobile.view.ui.Login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.FragmentLoginEmailBinding
import app.brainpool.nodesmobile.view.state.ViewState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

lateinit var viewModel: LoginViewModel
lateinit var binding: FragmentLoginEmailBinding

@AndroidEntryPoint
class LoginEmailFragment : Fragment(R.layout.fragment_login_email) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginEmailBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.btnSubmit.setOnClickListener {
            viewModel.login(binding.editTextEmail.text.toString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.login.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.fetchProgress.visibility = View.VISIBLE
                }
                is ViewState.Success -> {
                    if (response.value?.data == null) {
                        MaterialAlertDialogBuilder(requireContext()).setMessage(
                            "Error:" + response?.value?.errors?.get(
                                0
                            )?.message
                        ).show()
                    } else {
                        MaterialAlertDialogBuilder(requireContext()).setMessage(
                            response.value?.data?.loginUserDataByEmail?.message
                        ).show()
                    }
                    binding.fetchProgress.visibility = View.GONE
                }
                is ViewState.Error -> {
                    binding.fetchProgress.visibility = View.GONE
                }

            }
        }
    }
}