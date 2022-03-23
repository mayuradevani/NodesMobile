package app.brainpool.nodesmobile.view.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.brainpool.nodesmobile.Repository.NodesMobRepository
import app.brainpool.nodesmobile.data.localdatastore.UserNodes
import app.brainpool.nodesmobile.util.doInBackground
import app.brainpool.nodesmobile.view.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class AboutViewModel @Inject constructor(
    private val repository: NodesMobRepository,
) : ViewModel() {

    private val _getProfile by lazy {
        MutableLiveData<ViewState<UserNodes>>()
    }
    val userProfile: LiveData<ViewState<UserNodes>>
        get() = _getProfile

    fun getUserProfileLocal() {
        doInBackground(_getProfile, "Unable to get user") {
            repository.getUserProfile()
        }
    }
}