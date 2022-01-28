package app.brainpool.nodesmobile.view.ui.map

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.brainpool.nodesmobile.CreateTrackerPositionDataMutation
import app.brainpool.nodesmobile.DownloadMapsQuery
import app.brainpool.nodesmobile.Repository.NodesMobRepository
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.type.LatLongInput
import app.brainpool.nodesmobile.type.TrackerPositionInput
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
class MapViewModel @Inject constructor(private val repository: NodesMobRepository) :
    ViewModel() {
    private val _downloadMaps by lazy {
        MutableLiveData<ViewState<Response<DownloadMapsQuery.Data>>>()
    }

    val downloadMaps: LiveData<ViewState<Response<DownloadMapsQuery.Data>>>
        get() = _downloadMaps

    fun downloadMaps(context: Context, filename: String) = viewModelScope.launch {
        doInBackground(_downloadMaps, "Unable to download map") {
            repository.downloadMapsQuery(context, filename)
        }
    }

    private val _tracker by lazy {
        MutableLiveData<ViewState<Response<CreateTrackerPositionDataMutation.Data>>>()
    }
    val tracker: LiveData<ViewState<Response<CreateTrackerPositionDataMutation.Data>>>
        get() = _tracker

    fun createTrackerPositionData(requireContext: Context, latLng: LatLongInput) {
        doInBackground(_tracker, "Didn't save tracker info") {
//            val bm: BatteryManager =
//                requireContext.getSystemService(BATTERY_SERVICE) as BatteryManager
            val batLevel =
                (requireContext.getSystemService(BATTERY_SERVICE) as BatteryManager).getIntProperty(
                    BatteryManager.BATTERY_PROPERTY_CAPACITY
                ).toString()
            repository.createTrackerPositionData(
                requireContext,
                data = TrackerPositionInput(
                    Prefs.getString(PrefsKey.LICENCE_NUMBER_ID),
                    Prefs.getString(PrefsKey.LICENCE_NUMBER_NAME),
                    latLng,
                    "MOBILE-USER-DEVICE",
                    batLevel
                )
            )
        }
    }
}