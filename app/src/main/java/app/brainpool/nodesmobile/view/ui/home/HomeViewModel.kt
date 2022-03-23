package app.brainpool.nodesmobile.view.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.Repository.NodesMobRepository
import app.brainpool.nodesmobile.UpdateOrStoreNotificationTokenMutation
import app.brainpool.nodesmobile.data.localdatastore.Property
import app.brainpool.nodesmobile.data.localdatastore.UserNodes
import app.brainpool.nodesmobile.type.NotificationDeviceType
import app.brainpool.nodesmobile.type.NotificationInput
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
        MutableLiveData<ViewState<UserNodes>>()
    }
    val userProfile: LiveData<ViewState<UserNodes>>
        get() = _getProfile

    fun getUserProfile(context: Context) = viewModelScope.launch {
        doInBackground(_getProfile, "Unable to get User Profile") {
            repository.getUserProfile(context)
        }
    }

//    private val _getAllMapsByPropertyId by lazy {
//        MutableLiveData<ViewState<Property>>()
//    }
//    val getAllMapsByPropertyId: LiveData<ViewState<Property>>
//        get() = _getAllMapsByPropertyId

//    fun getAllMapsByPropertyId(context: Context, propertyId: String) = viewModelScope.launch {
//        doInBackground(_getAllMapsByPropertyId, "Unable to get Maps") {
//            repository.getAllMapsByPropertyIdQuery(context, propertyId)
//        }
//    }

    private val _getAllProperties by lazy {
        MutableLiveData<ViewState<MutableList<Property>>>()
    }
    val getAllProperties: LiveData<ViewState<MutableList<Property>>>
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

    fun updateOrStoreNotificationToken(context: Context, token: String, user: UserNodes) =
        viewModelScope.launch {
            doInBackground(_main, context.getString(R.string.unable_to_send_token)) {
                repository.updateOrStoreNotificationToken(
                    context,
                    data =
                    NotificationInput(
                        user.id,
                        NotificationDeviceType.MOBILE,
                        token
                    )
                )
            }
        }

    fun getAllPropertiesLocal() {
        doInBackground(_getAllProperties, "Unable to get properties") {
            repository.getAllProperties()
        }
    }

    fun getUserProfileLocal() {
        doInBackground(_getProfile, "Unable to get user") {
            repository.getUserProfile()
        }
    }
}