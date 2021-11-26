package app.brainpool.nodesmobile.view.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.brainpool.nodesmobile.LoginMutation
import app.brainpool.nodesmobile.Repository.NodesMobRepository
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

    fun login(email: String) = viewModelScope.launch {
        doInBackground(_login, "Unable to login") {
            repository.queryLoginWithEmail(email)
        }
    }
}