package app.brainpool.nodesmobile.view.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.brainpool.nodesmobile.LoginMutation
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.repository.NodesMobRepository
import app.brainpool.nodesmobile.util.doInBackground
import app.brainpool.nodesmobile.view.state.ViewState
import com.apollographql.apollo.api.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: NodesMobRepository,
) : ViewModel() {

    private val _login by lazy {
        MutableLiveData<ViewState<Response<LoginMutation.Data>>>()
    }
    val login: LiveData<ViewState<Response<LoginMutation.Data>>>
        get() = _login

    fun login(context: Context, email: String) = viewModelScope.launch {
        doInBackground(_login, context.getString(R.string.unable_login)) {
            repository.loginWithEmail(context, email)
        }
    }
}