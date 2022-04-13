package app.brainpool.nodesmobile.view.ui.map
import android.Manifest
import android.annotation.SuppressLint
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
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
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

@AndroidEntryPoint
class MapFragment : BaseFragment(R.layout.map_fragment),
    OnMapReadyCallback
//    ,GoogleMap.OnMarkerClickListener
{
    lateinit var binding: MapFragmentBinding
    private var overlay: TileOverlay? = null

    @ExperimentalCoroutinesApi
    private val viewModel by viewModels<MapViewModel>()
    private lateinit var googleMap: GoogleMap
    private var minimumZoom = 3.0f
    private val maximumZoom = 18.0f
    private lateinit var userNodes: UserNodes
    private lateinit var property: Property
    var folderName = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocationSent: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private var startedLocUpdate = false
    private var downcalled = false

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

                    if (Prefs.getBoolean(
                            PrefsKey.DEVICE_TRACKING,
                            true
                        ) && WifiService.instance.isOnline()
                    ) {
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
        }

        return binding.root
    }

    private fun setUI() {
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

    override fun onResume() {
        super.onResume()
//        if (!locationUpdateState && ::property.isInitialized) {
//            startLocationUpdates()
//        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun startLocationUpdates() {
        try {
            locationUpdateState = true
            if (!::locationRequest.isInitialized) {
                createLocationRequest()
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )
                    return
                }
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
            startedLocUpdate = true
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
                    priority =
                        LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

                val client = LocationServices.getSettingsClient(requireActivity())
                val task = client.checkLocationSettings(builder.build())

                task.addOnSuccessListener {
                    if (!locationUpdateState) {
                        startLocationUpdates()
                    }
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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsStatusMap ->
            // permissionStatusMap is of type <String, Boolean>
            // if all permissions accepted
            if (!permissionsStatusMap.containsValue(false)) {
                if (hasPermissions(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) && hasPermissions(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) && hasPermissions(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    if (::property.isInitialized && !downcalled) {
                        downloadMaps()
                    }
                    if (!locationUpdateState || !startedLocUpdate) {
                        startLocationUpdates()
                    }
                }
            } else {
                materialDialog(
                    getString(R.string.permission_needed_rationale),
                    "",
                    getString(R.string.ok)
                ) {
                    activity?.onBackPressed()
                }
            }

        }

    private fun downloadMaps() {
        downcalled = true
        val fName = property.fileName //Prefs.getString(PrefsKey.MAP_TILE_FILE_NAME)
        val mapId = property.mapId
        if (mapId.isNotEmpty() && mapId != "null")
            viewModel.downloadMaps(activity as MainActivity, mapId, fName)
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
                folderName = if (property.fileName.contains("."))
                    property.fileName.split(".")[0]
                else
                    ""
                if (folderName.nonEmpty()) {
                    if (WifiService.instance.isOnline() && !downcalled && hasPermissions(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        downloadMaps()
                    }
                    if (!locationUpdateState) {
                        startLocationUpdates()
                    }
                }
            }
        }
        observeViewState(viewModel.downloadMaps, binding.fetchProgress) { listMapTiles ->
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        if (!this::googleMap.isInitialized) {
            googleMap = map
            googleMap.isMyLocationEnabled = true
            googleMap.setMaxZoomPreference(maximumZoom)
            googleMap.setMinZoomPreference(minimumZoom)

            googleMap.uiSettings.isTiltGesturesEnabled = false
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isZoomGesturesEnabled = true
//            googleMap.setOnMapClickListener {
//                val marker = googleMap.addMarker(
//                    MarkerOptions()
//                        .position(it).zIndex(2.0f)
//                )
//                if (context != null) {
//                    val bitmap =
//                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_marker)
//                            ?.toBitmap()
//                    if (bitmap != null) {
//                        marker?.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
//                        marker?.tag = it.latitude.toString() + "\n" + it.longitude
//                        marker?.showInfoWindow()
//                    }
//                }
//            }
//            googleMap.setOnMarkerClickListener(this)

//            googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
//
//                override fun getInfoWindow(arg0: Marker): View? {
//                    return null
//                }
//
//                override fun getInfoContents(marker: Marker): View {
//                    val inflater = LayoutInflater.from(context)
//                    val v: View = inflater.inflate(R.layout.modal_window, null)
//                    val tvLat = v.findViewById<View>(R.id.latValue) as TextView
//                    val tvLng = v.findViewById<View>(R.id.longValue) as TextView
//                    tvLat.text = marker.position.latitude.toString()
//                    tvLng.text = marker.position.longitude.toString()
//                    v.findViewById<View>(R.id.tvCancel)
//                        .setOnClickListener { marker.hideInfoWindow() }
//                    return v
//                }
//            })
//            googleMap.setOnInfoWindowClickListener(this);
            addOverlayOnMap()
            activity?.let {
                LocationServices.getFusedLocationProviderClient(it).lastLocation.addOnSuccessListener(
                    it
                ) { location ->
                    if (location != null) {
                        lastLocationSent = location
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                maximumZoom
                            )
                        )
                    }
                }
            }
        }
    }

    private fun addOverlayOnMap() {
        if (WifiService.instance.isOnline())
            addOverlay()
        else {//offline and map not found
            val mapDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/.NodesMobile/" + folderName //Prefs.getString(PrefsKey.MAP_TILE_FOLDER)
            )
            val count = getAllImageFilesInFolder(mapDir)
            Log.v(TAG, "Total Images: $count")
            if (count == 0) {
                materialDialog(getString(R.string.map_not_downloaded), "", "OK")
                {
                    it.dismiss()
                }
            } else {
                addOverlay()
            }
        }
    }

    private fun addOverlay() {
        if (overlay == null) {
            overlay = googleMap.addTileOverlay(
                TileOverlayOptions().tileProvider(
                    CustomMapTileProvider(
                        requireContext(), folderName
                    )
                )
            )!!
            overlay?.isVisible =
                Prefs.getString(PrefsKey.MAP_TYPE) != getString(R.string.device)
        }
    }

//    lateinit var dialog: Dialog
//    lateinit var rvImages: RecyclerView
//    override fun onMarkerClick(marker: Marker): Boolean {
////        val viewPosition: Point = googleMap.projection.toScreenLocation(marker.position)
////
////        val balloon = Balloon.Builder(requireActivity())
////            .setLayout(R.layout.modal_window)
////            .setCornerRadius(4f)
////            .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent_modal))
////            .setBalloonAnimation(BalloonAnimation.CIRCULAR)
////            .build()
////        balloon.show(binding.tvMapType, viewPosition.x, viewPosition.y)
////
////        val tvLat = balloon.getContentView().findViewById<View>(R.id.latValue) as TextView
////        val tvLng = balloon.getContentView().findViewById<View>(R.id.longValue) as TextView
////        (balloon.getContentView()
////            .findViewById<View>(R.id.tvImages) as TextView).setOnClickListener {
////            openImagePicker()
////        }
////        tvLat.text = marker.position.latitude.toString()
////        tvLng.text = marker.position.longitude.toString()
////
////        balloon.getContentView().findViewById<View>(R.id.tvCancel)
////            .setOnClickListener { balloon.dismiss() }
//
//        dialog = Dialog(requireActivity())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.modal_window)
//        dialog.window?.setBackgroundDrawableResource(R.color.transparent_modal)
//
//        val tvLat = dialog.findViewById<View>(R.id.latValue) as TextView
//        val tvLng = dialog.findViewById<View>(R.id.longValue) as TextView
//        (dialog.findViewById<View>(R.id.tvImages) as TextView).setOnClickListener {
//            openImagePicker()
//        }
//        tvLat.text = marker.position.latitude.toString()
//        tvLng.text = marker.position.longitude.toString()
//
//        dialog.findViewById<View>(R.id.tvCancel)
//            .setOnClickListener { dialog.dismiss() }
//        rvImages = dialog.findViewById<View>(R.id.rv_images) as RecyclerView
//        rvImages.layoutManager = GridLayoutManager(activity, 3)
//        rvImages.isNestedScrollingEnabled = false
//        adapter = ImagesAdapter(ArrayList(), true)
//        rvImages.adapter = adapter
//
//        dialog.findViewById<View>(R.id.tvSave)
//            .setOnClickListener {
//                materialDialog(adapter.data.size.toString()+""+adapter.data.get(0).uri, "", "OK") {
//                    it.dismiss()
//                }
//                //dialog.dismiss()
//            }
//
//        dialog.show()
//        return true
//    }
//
//    lateinit var adapter: ImagesAdapter
//    private fun useUri(imageUri: Uri?) {
//        adapter.addData(Images(imageUri, "", false, true))
//        adapter.notifyItemInserted(adapter.itemCount)
//    }
//
//    private fun openImagePicker() {
//        ImagePicker.with(this)
//            .crop() //Crop image(Optional), Check Customization for more option
//            .compress(1024) //Final image size will be less than 1 MB(Optional)
//            .maxResultSize(
//                1080,
//                1080
//            ) //Final image resolution will be less than 1080 x 1080(Optional)
//            .createIntent { intent ->
//                activityResultLauncher.launch(intent)
//                null
//            }
//    }
//
//    private var activityResultLauncher = registerForActivityResult(
//        StartActivityForResult()
//    ) { result ->
//        when (result.resultCode) {
//            Activity.RESULT_OK -> {
//                val data = result.data
//                try {
//                    val uri = data!!.data
//                    useUri(uri)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//            ImagePicker.RESULT_ERROR -> {
//                Toast.makeText(activity, ImagePicker.getError(result.data), Toast.LENGTH_SHORT)
//                    .show()
//            }
//            else -> {
////                Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}
