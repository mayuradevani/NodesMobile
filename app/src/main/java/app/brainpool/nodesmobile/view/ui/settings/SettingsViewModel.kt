package app.brainpool.nodesmobile.view.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.brainpool.nodesmobile.LogoutUserDataQuery
import app.brainpool.nodesmobile.UpdateStatusTrackerDataMutation
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
class SettingsViewModel @Inject constructor(private val repository: NodesMobRepository) :
    ViewModel() {
    private val _logout by lazy {
        MutableLiveData<ViewState<Response<LogoutUserDataQuery.Data>>>()
    }
    val logout: LiveData<ViewState<Response<LogoutUserDataQuery.Data>>>
        get() = _logout

    fun logout(context: Context) = viewModelScope.launch {
        doInBackground(_logout, "Unable to Logout") {
            repository.logout(context)
        }
    }

    private val _updateLocUpdateStatus by lazy {
        MutableLiveData<ViewState<Response<UpdateStatusTrackerDataMutation.Data>>>()
    }
    val updateLocUpdateStatus: LiveData<ViewState<Response<UpdateStatusTrackerDataMutation.Data>>>
        get() = _updateLocUpdateStatus

    fun updateLocUpdateStatus(context: Context, isActive: Boolean) = viewModelScope.launch {
        doInBackground(_updateLocUpdateStatus) {
            val user = repository.getUserProfile()
            repository.updateStatusTrackerData(context, user.imei.toString(), isActive)
        }
    }

}

