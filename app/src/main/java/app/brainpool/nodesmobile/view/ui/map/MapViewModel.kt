package app.brainpool.nodesmobile.view.ui.map

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.brainpool.nodesmobile.CreateTrackerPositionDataMutation
import app.brainpool.nodesmobile.Repository.NodesMobRepository
import app.brainpool.nodesmobile.data.localdatastore.MapTileNodes
import app.brainpool.nodesmobile.data.localdatastore.Property
import app.brainpool.nodesmobile.data.localdatastore.UserNodes
import app.brainpool.nodesmobile.type.LatLongInput
import app.brainpool.nodesmobile.type.TrackerPositionInput
import app.brainpool.nodesmobile.util.doInBackground
import app.brainpool.nodesmobile.view.state.ViewState
import com.apollographql.apollo.api.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MapViewModel @Inject constructor(private val repository: NodesMobRepository) :
    ViewModel() {
    private val _downloadMaps by lazy {
        MutableLiveData<ViewState<MutableList<MapTileNodes>>>()
    }

    val downloadMaps: LiveData<ViewState<MutableList<MapTileNodes>>>
        get() = _downloadMaps

    fun downloadMaps(context: Context, mapId: String, filename: String) = viewModelScope.launch {
        doInBackground(_downloadMaps, "Unable to download map") {
            repository.downloadMapsQuery(context, mapId, filename)
        }
    }

    private val _tracker by lazy {
        MutableLiveData<ViewState<Response<CreateTrackerPositionDataMutation.Data>>>()
    }
    val tracker: LiveData<ViewState<Response<CreateTrackerPositionDataMutation.Data>>>
        get() = _tracker

    fun createTrackerPositionData(requireContext: Context, latLng: LatLongInput) {
        doInBackground(_tracker, "Didn't save tracker info") {
            val batLevel =
                (requireContext.getSystemService(BATTERY_SERVICE) as BatteryManager).getIntProperty(
                    BatteryManager.BATTERY_PROPERTY_CAPACITY
                ).toString()
            val user = repository.getUserProfile()
            repository.createTrackerPositionData(
                requireContext,
                data = TrackerPositionInput(
                    user.licenseNumberId,
                    user.licenseNumberName,
                    latLng,
                    user.imei.toString(),
                    batLevel
                )
            )
        }
    }

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

    private val _getProp by lazy {
        MutableLiveData<ViewState<Property>>()
    }
    val getProp: LiveData<ViewState<Property>>
        get() = _getProp

    fun getProperty(pId: String) = viewModelScope.launch {
        doInBackground(_getProp, "Unable to get properties") {
            repository.getProperty(pId) ?: Property()
        }
    }

    fun getLocalMaps(mapId: String) = viewModelScope.launch {
        doInBackground(_downloadMaps, "Unable to fetch map tiles") {
            repository.getAllMaps(mapId)
        }
    }


    private val _propUpdate by lazy {
        MutableLiveData<ViewState<Property>>()
    }
    val propUpdate: LiveData<ViewState<Property>>
        get() = _propUpdate

    fun updatePropertyNotification(p: Property) = viewModelScope.launch {
        doInBackground(_propUpdate, "Unable to get properties") {
            repository.updatePropertyNotification(p) ?: Property()
        }
    }

}