package app.brainpool.nodesmobile.view.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.LoginFragmentBinding
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.materialDialog
import app.brainpool.nodesmobile.view.state.ViewState
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.login_fragment) {

    lateinit var viewModel: LoginViewModel
    lateinit var binding: LoginFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            binding = LoginFragmentBinding.inflate(inflater)
            viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
            binding.btnSubmit.setOnClickListener {
                binding.edtEmail.validEmail {
                    binding.edtEmail.error = it
                }.apply { if (!this) return@setOnClickListener }

                viewModel.login(binding.edtEmail.text.toString())
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
        viewModel.login.observe(this) {
            when (it) {
                is ViewState.Loading -> {
                    binding.fetchProgress.visibility = View.VISIBLE
                }
                is ViewState.Success -> {
                    if (it.value?.data == null) {
                        materialDialog(it.value?.errors?.get(0)?.message.toString(),"","OK"                        ){
                            it.dismiss()
                        }
                    } else {
                        findNavController().navigate(R.id.holdingFragment)
                    }
                    binding.fetchProgress.gone()
                }
                is ViewState.Error -> {
                    materialDialog(it?.message.toString(), "", "OK") {
                        it.dismiss()
                    }
                    binding.fetchProgress.gone()
                }
            }
        }
    }
}