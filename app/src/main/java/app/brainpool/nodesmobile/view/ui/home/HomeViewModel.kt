package app.brainpool.nodesmobile.view.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.brainpool.nodesmobile.*
import app.brainpool.nodesmobile.Repository.NodesMobRepository
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.type.NotificationDeviceType
import app.brainpool.nodesmobile.type.NotificationInput
import app.brainpool.nodesmobile.util.doInBackground
import app.brainpool.nodesmobile.view.state.ViewState
import com.apollographql.apollo.api.Response
import com.pixplicity.easyprefs.library.Prefs
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

    private val _getAllProperties by lazy {
        MutableLiveData<ViewState<Response<GetAllPropertiesQuery.Data>>>()
    }
    val getAllProperties: LiveData<ViewState<Response<GetAllPropertiesQuery.Data>>>
        get() = _getAllProperties

    fun getAllProperties(context: Context) = viewModelScope.launch {
        doInBackground(_getAllProperties, "Unable to get properties") {
            repository.getAllProperties(context)
        }
    }

    private val _main by lazy {
        MutableLiveData<ViewState<Response<UpdateOrStoreNotificationTokenMutation.Data>>>()
    }
    val main: LiveData<ViewState<Response<UpdateOrStoreNotificationTokenMutation.Data>>>
        get() = _main

    fun sendToken(context: Context, token: String) = viewModelScope.launch {
        doInBackground(_main, context.getString(R.string.unable_to_send_token)) {
            repository.updateOrStoreNotificationToken(
                context,
                data =
                NotificationInput(
                    Prefs.getString(PrefsKey.USER_ID),
                    NotificationDeviceType.MOBILE,
                    token
                )
            )
        }
    }
}