package app.brainpool.nodesmobile.view.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.brainpool.nodesmobile.MainActivity
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.MapFragmentBinding
import app.brainpool.nodesmobile.type.LatLongInput
import app.brainpool.nodesmobile.util.*
import app.brainpool.nodesmobile.util.GlobalVar.TAG
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.material.snackbar.Snackbar
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File


@AndroidEntryPoint
class MapFragment : Fragment(R.layout.map_fragment),
    OnMapReadyCallback {

    lateinit var binding: MapFragmentBinding
    var overlay: TileOverlay? = null

    @ExperimentalCoroutinesApi
    private val viewModel by viewModels<MapViewModel>()

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    var locationUpdateState = false

    private var MinimumZoom = 3.0f
    private val MaximumZoom = 18.0f

    private lateinit var centerLatLong: LatLng

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    val mapDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .toString() + "/.NodesMobile/" + Prefs.getString(PrefsKey.MAP_TILE_FOLDER)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                try {
                    Log.v(
                        TAG,
                        "Last Location: ${p0.lastLocation.latitude} and ${p0.lastLocation.longitude}"
                    )
                    if (Prefs.getBoolean(PrefsKey.DEVICE_TRACKING)) {
                        val results = FloatArray(1)
                        Location.distanceBetween(
                            lastLocation.latitude, lastLocation.longitude,
                            p0.lastLocation.latitude, p0.lastLocation.longitude, results
                        )
//                        if (results.get(0) >= Prefs.getString(PrefsKey.RADIUS).toInt()) {
                            lastLocation = p0.lastLocation
                            Log.v(
                                TAG,
                                "Tracking Location: ${lastLocation.latitude} and ${lastLocation.longitude}"
                            )
                            createTrackerPositionData(lastLocation.latitude, lastLocation.longitude)
//                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (this::binding.isInitialized) {
            binding
        } else {
            binding = MapFragmentBinding.inflate(inflater)
            setUI()
        }
        observeLiveData()
        return binding.root
    }

    private fun setUI() {
        centerLatLong = LatLng(
            Prefs.getDouble(PrefsKey.MAP_CENTER_LATI),
            Prefs.getDouble(PrefsKey.MAP_CENTER_LONGI)
        )
        binding.tvSiteNotes.setOnClickListener {
            (activity as MainActivity).binding.bottomNavigation.selectedItemId =
                R.id.siteNotesFragment
        }
        if (Prefs.getString(PrefsKey.MAP_TYPE, "") == "")
            Prefs.putString(PrefsKey.MAP_TYPE, getString(R.string.device))
        setTextAndColor()
        binding.tvMapType.setOnClickListener {
            showMaptiles()
        }
        binding.ivUpDown.setOnClickListener { binding.tvMapType.performClick() }
        if (Prefs.getBoolean(PrefsKey.UPDATE_MAP) //This is used for map update notification received from server
            || Prefs.getString(PrefsKey.DEFAULT_MAP,getString(R.string.newest)) == getString(R.string.newest)//This is used for map update settings
//            || (Prefs.getString(PrefsKey.DATA_USAGE)
//                .equals(getString(R.string.wifiOnly)) && context?.isWifiNetworkConnected() == true)//This is used for wifi/data usage settings
        ) {
            checkPermissionAndDownloadMapTiles()
            Prefs.putBoolean(PrefsKey.UPDATE_MAP, false)
        }
    }

    private fun showMaptiles() {
        if (Prefs.getString(PrefsKey.MAP_TYPE) == getString(R.string.device)) {
            overlay?.isVisible = true
            Prefs.putString(PrefsKey.MAP_TYPE, getString(R.string.overlay))
        } else {
            overlay?.isVisible = false
            Prefs.putString(PrefsKey.MAP_TYPE, getString(R.string.device))
        }
        setTextAndColor()
    }

    private fun setTextAndColor() {
        if (Prefs.getString(PrefsKey.MAP_TYPE) == getString(R.string.device)) {
            binding.tvMapType.text = getString(R.string.device)
            binding.tvMapType.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey_text
                )
            )
        } else {
            binding.tvMapType.text = getString(R.string.overlay)
            binding.tvMapType.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green_text
                )
            )
        }
    }


    internal fun startLocationUpdates() {
        try {
            if (context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
                return
            }
            if (!::locationRequest.isInitialized) {
                createLocationRequest()

            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper() /* Looper */
            )
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createLocationRequest() {
        try {
            var time = Prefs.getString(PrefsKey.TIME_INTERVAL, "5")
            if (time == "null")
                time = "5"
            locationRequest = LocationRequest.create().apply {
                interval = (1000 * time.toLong())
                fastestInterval = (1000 * time.toLong())
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

            val client = LocationServices.getSettingsClient(requireActivity())
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                locationUpdateState = true
                startLocationUpdates()
            }
            task.addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        activity?.let {
                            e.startResolutionForResult(
                                it,
                                GlobalVar.REQUEST_CHECK_SETTINGS
                            )
                        }
                    } catch (sendEx: IntentSender.SendIntentException) {
                        sendEx.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermissionAndDownloadMapTiles() {
        if (requireActivity().hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            && requireActivity().hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            downloadMaps()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GlobalVar.REQUEST_CHECK_SETTINGS ->
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        locationUpdateState = true
                        startLocationUpdates()
                    }
                    AppCompatActivity.RESULT_CANCELED -> {
                        activity?.finish()
                    }
                    else -> {
                    }
                }
        }
    }

    private fun downloadMaps() {
        val fName = Prefs.getString(PrefsKey.MAP_TILE_FILE_NAME)
        if (!fName.isNullOrEmpty() && fName != "null")
            viewModel.downloadMaps(requireContext(), Prefs.getString(PrefsKey.MAP_TILE_FILE_NAME))
    }

    private fun createTrackerPositionData(latitude: Double, longitude: Double) {
        viewModel.createTrackerPositionData(requireContext(), LatLongInput(latitude, longitude))
    }

    private fun observeLiveData() {
        observeViewState(viewModel.downloadMaps) { response ->
            if (response != null) {
                if (response?.data == null) {
                    materialDialog(response.errors?.get(0)?.message.toString(), "", "OK") {
                        it.dismiss()
                    }
                } else {
                    val mapTileList = response?.data?.downloadMaps
                    if (mapTileList != null) {
                        val count = getAllImageFilesInAllFolder(mapDir)
                        Log.v(TAG, "Total Images: $count")
                        if (mapTileList.size != count) {
                            Log.v(TAG, "Downloading Map tiles")
                            for (p in mapTileList) {
                                var fName = p?.link.toString()
                                fName = fName.substring(fName.indexOf("maptiles/") + 9)
                                CoroutineScope(Dispatchers.IO).launch {
                                    saveImage(
                                        Glide.with(requireContext())
                                            .asBitmap()
                                            .load(p?.link.toString()) // sample image
                                            .submit()
                                            .get(), fName
                                    )
                                }
                            }
                            Log.v(TAG, "Downloading Map tiles complete")
                        }
                    }
                }
            }
        }
        observeViewState(viewModel.tracker) { response ->
            if (response != null) {
                if (response?.data == null) {
                    Log.v(TAG, response.errors?.get(0)?.message.toString())
                } else {
                    Log.v(TAG, "success")
                }
            }
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                downloadMaps()
            } else {
                binding.tvMapType.showSnackbar(
                    binding.tvMapType,
                    getString(R.string.read_write_permission_rationale),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.ok)
                ) {
                    checkPermissionAndDownloadMapTiles()
                }
            }
        }

    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCode && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createLocationRequest()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        if (!this::googleMap.isInitialized) {
            googleMap = map
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.setMaxZoomPreference(MaximumZoom)
            googleMap.setMinZoomPreference(MinimumZoom)

            googleMap.uiSettings.isTiltGesturesEnabled = false
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isZoomGesturesEnabled = true
            overlay = map.addTileOverlay(
                TileOverlayOptions().tileProvider(
                    CustomMapTileProvider(
                        requireContext()
                    )
                )
            )!!

            googleMap.isMyLocationEnabled = true
            activity?.let {
                fusedLocationClient.lastLocation.addOnSuccessListener(it) { location ->
                    if (location != null) {
                        lastLocation = location
                        centerLatLong = LatLng(lastLocation.latitude, lastLocation.longitude)
                    }
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            centerLatLong,
                            MaximumZoom
                        )
                    )
                }
            }
        }
    }
}
