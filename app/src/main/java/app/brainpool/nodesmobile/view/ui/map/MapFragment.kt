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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.data.localdatastore.Property
import app.brainpool.nodesmobile.data.localdatastore.UserNodes
import app.brainpool.nodesmobile.data.models.AppNotification
import app.brainpool.nodesmobile.databinding.MapFragmentBinding
import app.brainpool.nodesmobile.type.LatLongInput
import app.brainpool.nodesmobile.util.*
import app.brainpool.nodesmobile.util.GlobalVar.TAG
import app.brainpool.nodesmobile.view.ui.MainActivity
import com.alcophony.app.ui.core.BaseFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
class MapFragment : BaseFragment(R.layout.map_fragment),
    OnMapReadyCallback {

    lateinit var binding: MapFragmentBinding
    private var overlay: TileOverlay? = null

    @ExperimentalCoroutinesApi
    private val viewModel by viewModels<MapViewModel>()

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocationSent: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    private var MinimumZoom = 3.0f
    private val MaximumZoom = 18.0f
    private lateinit var userNodes: UserNodes
    private lateinit var property: Property
    private lateinit var centerLatLong: LatLng
    var folder = ""
    private val extras by navArgs<MapFragmentArgs>()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

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
                    binding.tvLocNorth.text = getDegree(p0.lastLocation.latitude) + "N"
                    binding.tvLocEast.text = getDegree(p0.lastLocation.longitude) + "E"

                    if (Prefs.getBoolean(PrefsKey.DEVICE_TRACKING, true)) {
                        if (!::lastLocationSent.isInitialized)
                            lastLocationSent = p0.lastLocation
                        val results = FloatArray(1)
                        Location.distanceBetween(
                            lastLocationSent.latitude, lastLocationSent.longitude,
                            p0.lastLocation.latitude, p0.lastLocation.longitude, results
                        )
                        if (userNodes.radius != null)
                            if (results[0] >= userNodes.radius!!) {
                                Log.v(
                                    TAG,
                                    "Tracking Location: ${p0.lastLocation.latitude} and ${p0.lastLocation.longitude}"
                                )
                                createTrackerPositionData(
                                    p0.lastLocation.latitude,
                                    p0.lastLocation.longitude
                                )
                                lastLocationSent = p0.lastLocation
                            }
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
    ): View {
        if (this::binding.isInitialized) {
            binding
        } else {
            binding = MapFragmentBinding.inflate(inflater)
            setUI()
            observeLiveData()
            viewModel.getUserProfileLocal()

            //AppNotification:
            arguments?.apply {
                MapFragmentArgs.fromBundle(this).appnotification.apply {
                    if (this != null) {
                        var noti: AppNotification? = this.fromJson()
                        if (noti?.mapFileName?.isNotEmpty() == true)//notification
                            updateMap(noti)
                        else
                            viewModel.getProperty(noti?.propertyId.toString())
                        Log.v(TAG, "P1: " + noti?.propertyId.toString())
                    }
                }
            }
//            arguments?.getString(GlobalVar.PROPERTY_ID).let {
//                viewModel.getProperty(it.toString())
//                Log.v(TAG,"P2: "+it.toString())
//            }
////            extras.propertyId?.let {
////                viewModel.getProperty(it)
////            }
//            activity?.getExtra<String>().toString().let { viewModel.getProperty(it)
//                Log.v(TAG,"P3: "+it.toString())}
        }

        return binding.root
    }

    private fun setUI() {
//        centerLatLong = LatLng(
//            Prefs.getDouble(PrefsKey.MAP_CENTER_LATI),
//            Prefs.getDouble(PrefsKey.MAP_CENTER_LONGI)
//        )
//        centerLatLong = LatLng(
//            property.centerLat,
//            property.centerLong
//        )
        binding.tvSiteNotes.setOnClickListener {
            (activity as MainActivity).binding.bottomNavigation.selectedItemId =
                R.id.siteNotesFragment
        }
        setTextAndColor()
        binding.tvMapType.setOnClickListener {
            showMaptiles()
        }
        binding.ivUpDown.setOnClickListener { binding.tvMapType.performClick() }

        binding.textClock.format24Hour = null
        binding.textClock.format12Hour = "yyyy.MM.dd\nhh:mm:ss a"
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


    private fun startLocationUpdates() {
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
                Looper.getMainLooper()
            )
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createLocationRequest() {
        try {
            val time = userNodes.timeInterval
            if (time != null) {
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
        if (::property.isInitialized) {
            val fName = property.fileName //Prefs.getString(PrefsKey.MAP_TILE_FILE_NAME)
            val mapId = property.mapId
            if (!mapId.isNullOrEmpty() && mapId != "null")
                if (WifiService.instance.isOnline())
                    viewModel.downloadMaps(requireContext(), mapId, fName)
                else
                    viewModel.getLocalMaps(mapId)
        }
    }

    private fun createTrackerPositionData(latitude: Double, longitude: Double) {
        viewModel.createTrackerPositionData(requireContext(), LatLongInput(latitude, longitude))
    }

    private fun updateMap(pId: AppNotification?) {
        val prop = Property().apply {
            id = pId?.propertyId.toString()
            fileName = pId?.mapFileName.toString()
            mapId = pId?.mapId.toString()
        }
        viewModel.updatePropertyNotification(prop)
    }

    private fun observeLiveData() {
        observeViewState(viewModel.userProfile, binding.fetchProgress) { user ->
            if (user != null) {
                userNodes = user
            }
        }
        observeViewState(viewModel.propUpdate, binding.fetchProgress) { prop ->
            if (prop != null) {
                viewModel.getProperty(prop.id)
            }
        }
        observeViewState(viewModel.getProp, binding.fetchProgress) { prop ->
            if (prop != null) {
                property = prop
//                folder = if (property.fileUrl.contains("/brainpoollicense/maptiles/"))
//                    property.fileUrl.split("/brainpoollicense/maptiles/")[1]
//                else
//                    ""
////                else if (property.fileName.contains(".png"))
////                    property.fileName.split(".png")[0]
////                else
////                    property.fileName.split(".tiff")[0]

//                folder = if (property.fileName.contains(".jpg"))
//                    property.fileName.split(".jpg")[0]
//                else if (property.fileName.contains(".png"))
//                    property.fileName.split(".png")[0]
//                else
//                    property.fileName.split(".tiff")[0]
                folder = if (property.fileName.contains("."))
                    property.fileName.split(".")[0]
                else
                    ""
                if (WifiService.instance.isOnline()  //This is used for map update notification received from server
                    || Prefs.getString(
                        PrefsKey.DEFAULT_MAP,
                        getString(R.string.newest)
                    ) == getString(R.string.newest)
                ) {
                    checkPermissionAndDownloadMapTiles()
                }
                if (!locationUpdateState) {
                    startLocationUpdates()
                }
            }
        }
        observeViewState(viewModel.downloadMaps) { mapTileList ->
            try {
                if (mapTileList?.size == 0)
                    materialDialog(getString(R.string.map_not_found), "", "OK")
                    {
                        it.dismiss()
                    }
                else if (mapTileList != null && WifiService.instance.isOnline()) {
                    if (WifiService.instance.isOnline()) {
                        val mapDir = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                .toString() + "/.NodesMobile/" + folder //Prefs.getString(PrefsKey.MAP_TILE_FOLDER)
                        )
                        val count = getAllImageFilesInFolder(mapDir)
                        Log.v(TAG, "Total Images: $count")
                        if (mapTileList.size != count) {
                            Log.v(TAG, "Downloading Map tiles")
                            for (p in mapTileList) {
                                var fName = p.link.toString()
                                fName = fName.substring(fName.indexOf("maptiles/") + 9)
                                val file = getFile(fName)
                                if (file == null || !file.exists())
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            saveImage(
                                                Glide.with(requireContext())
                                                    .asBitmap()
                                                    .load(p?.link.toString())
                                                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                    .submit()
                                                    .get(), fName
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Log.v(TAG, "Error:$fName")
                                        }
                                    }
                            }
                            Log.v(TAG, "Downloading Map tiles complete")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        observeViewState(viewModel.tracker) { response ->
            if (response != null) {
                if (response.data == null) {
                    Log.v(TAG, response.errors?.get(0)?.message.toString())
                } else {
                    Log.v(TAG, "success")
                }
            }
        }
    }

    private val requestPermissionLauncher =
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
        if (!locationUpdateState && ::property.isInitialized) {
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
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.setMaxZoomPreference(MaximumZoom)
            googleMap.setMinZoomPreference(MinimumZoom)

            googleMap.uiSettings.isTiltGesturesEnabled = false
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isZoomGesturesEnabled = true
            if (WifiService.instance.isOnline())
                addOverlayOnMap(googleMap)
            else {
                val mapDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .toString() + "/.NodesMobile/" + folder //Prefs.getString(PrefsKey.MAP_TILE_FOLDER)
                )
                val count = getAllImageFilesInFolder(mapDir)
                Log.v(TAG, "Total Images: $count")
                if (count == 0) {
                    materialDialog(getString(R.string.map_not_downloaded), "", "OK")
                    {
                        it.dismiss()
                    }
                } else {
                    addOverlayOnMap(googleMap)
                }
            }
            activity?.let {
                fusedLocationClient.lastLocation.addOnSuccessListener(it) { location ->
                    if (location != null) {
                        centerLatLong = LatLng(location.latitude, location.longitude)
                        lastLocationSent = location
                        googleMap.moveCamera(
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

    private fun addOverlayOnMap(map: GoogleMap) {
        overlay = map.addTileOverlay(
            TileOverlayOptions().tileProvider(
                CustomMapTileProvider(
                    requireContext(), folder
                )
            )
        )!!
        overlay?.isVisible =
            Prefs.getString(PrefsKey.MAP_TYPE) != getString(R.string.device)
    }

//    fun refreshForMapUpdate(pId: String) {
//        viewModel.getProperty(pId)
//    }
}
