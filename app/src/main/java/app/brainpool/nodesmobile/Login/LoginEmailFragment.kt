package app.brainpool.nodesmobile.Login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.FragmentLoginEmailBinding
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.example.LoginMutation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

lateinit var viewModel: LoginViewModel
lateinit var binding: FragmentLoginEmailBinding

class LoginEmailFragment : Fragment(R.layout.fragment_login_email), CoroutineScope {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentLoginEmailBinding>(
            inflater,
            R.layout.fragment_login_email,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.btnSubmit.setOnClickListener {
            launch { callApi(binding.editTextEmail.text.toString()) }
        }
        return binding.root
    }

    private suspend fun callApi(email: String) {
        val apolloClient = ApolloClient.builder()
            .serverUrl("http://34.126.80.190/graph-private")
            .build()

        val response =
            try {
                apolloClient.mutate(LoginMutation(email = email)).await()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        val login = response?.data
        if (login == null || response.hasErrors()) {
            MaterialAlertDialogBuilder(requireContext()).setMessage(
                "Error:" + response?.errors?.get(
                    0
                )?.message
            ).show()
        } else {
            MaterialAlertDialogBuilder(requireContext()).setMessage(
                login.loginUserDataByEmail?.message
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

}