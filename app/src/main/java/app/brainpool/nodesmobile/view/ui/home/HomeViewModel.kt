package app.brainpool.nodesmobile.view.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.brainpool.nodesmobile.GetAllMapsByPropertyIdQuery
import app.brainpool.nodesmobile.GetUserProfileQuery
import app.brainpool.nodesmobile.LogoutUserDataQuery
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
class HomeViewModel @Inject constructor(
    private val repository: NodesMobRepository,
) : ViewModel() {

    private val _getProfile by lazy {
        MutableLiveData<ViewState<Response<GetUserProfileQuery.Data>>>()
    }
    val userProfile: LiveData<ViewState<Response<GetUserProfileQuery.Data>>>
        get() = _getProfile

    fun getUserProfile(context: Context) = viewModelScope.launch {
        doInBackground(_getProfile, "Unable to get User Profile") {
            repository.getUserProfile(context)
        }
    }

    private val _getAllMapsByPropertyId by lazy {
        MutableLiveData<ViewState<Response<GetAllMapsByPropertyIdQuery.Data>>>()
    }
    val getAllMapsByPropertyId: LiveData<ViewState<Response<GetAllMapsByPropertyIdQuery.Data>>>
        get() = _getAllMapsByPropertyId

    fun getAllMapsByPropertyId(context: Context, propertyId: String) = viewModelScope.launch {
        doInBackground(_getAllMapsByPropertyId, "Unable to get Maps") {
            repository.getAllMapsByPropertyIdQuery(context, propertyId)
        }
    }

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
}