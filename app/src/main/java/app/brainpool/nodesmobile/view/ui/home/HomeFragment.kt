package app.brainpool.nodesmobile.view.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import app.brainpool.nodesmobile.GetAllPropertiesQuery
import app.brainpool.nodesmobile.MainActivity
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.data.models.HomeListItem
import app.brainpool.nodesmobile.databinding.HomeFragmentBinding
import app.brainpool.nodesmobile.util.*
import com.alcophony.app.ui.core.BaseFragment
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.home_fragment) {
    lateinit var binding: HomeFragmentBinding
    private val viewModel by viewModels<HomeViewModel>()
    var proprtyList: List<GetAllPropertiesQuery.GetAllProperty?>? = null
    var pIdLastSelected: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            createChannel(
                getString(R.string.app_notification_channel_id),
                getString(R.string.app_notification_channel_name)
            )
            binding = HomeFragmentBinding.inflate(inflater)
            binding.btnStart.setOnClickListener {
                binding.btnStart.text = getString(R.string.please_wait)
                goToMain()
            }

//            binding.ivMap.setOnClickListener {
//                goToMain()
//            }
//            binding.recyclerView.apply {
//                hasFixedSize()
//                layoutManager = GridLayoutManager(context, 2)
//                adapter = HomeListAdapter(DataServer.getHomeData())
//            }
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val p: GetAllPropertiesQuery.GetAllProperty? = proprtyList?.get(position)
//                    Prefs.putString(PrefsKey.PROPERTY_ID, p?.id)
                    if (WifiService.instance.isOnline())
                        viewModel.getAllMapsByPropertyId(
                            requireContext(),
                            p?.id.toString()
                        )
                }
            }
            if (!Prefs.getBoolean(PrefsKey.SENT_TOKEN, false)) {
                setPushNotificationToken()
            }
            if (WifiService.instance.isOnline()) {
                viewModel.getUserProfile(requireContext())
                viewModel.getAllProperties(requireContext())
                binding.btnStart.gone()
            } else {
                binding.tvUserName.text = Prefs.getString(PrefsKey.NAME)
                binding.tvRole.text = Prefs.getString(PrefsKey.ROLE)
                pIdLastSelected = Prefs.getString(PrefsKey.DEF_PROPERTY_ID)
                proprtyList = Prefs.getString(PrefsKey.PROPERTIES, proprtyList.toString()).getList()
                setPropertiesSpinner()
                binding.btnStart.visible()
            }
            observeLiveData()
            return binding.root
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return container
    }

    private fun setPushNotificationToken() {
        FirebaseMessaging.getInstance()
            .token.addOnSuccessListener {
                viewModel.sendToken(requireContext(), it)
            }
    }

    override fun onResume() {
        super.onResume()
        binding.btnStart.text = getString(R.string.log_in)
    }

    private fun goToMain() {
        activity?.navigate<MainActivity>()
    }

    private fun observeLiveData() {
        observeViewState(viewModel.main, binding.fetchProgress) { response ->
            if (response != null) {
                if (response.data != null) {
                    Prefs.putBoolean(PrefsKey.SENT_TOKEN, true)
                    Log.v(GlobalVar.TAG, "Token sent:" + response.data)
                }
                binding.fetchProgress.gone()
            }
        }
        observeViewState(viewModel.userProfile, binding.fetchProgress) { response ->
            if (response != null) {
                if (response?.data == null) {
                    materialDialog(response.errors?.get(0)?.message.toString(), "", "OK") {
                        it.dismiss()
                    }
                } else {
                    val user = response?.data?.getUserProfile
                    if (user?.licensenumber?.id?.isNotEmpty() == true) {
                        Prefs.putString(PrefsKey.LICENCE_NUMBER_ID, user?.licensenumber?.id)
                        Prefs.putString(PrefsKey.LICENCE_NUMBER_NAME, user?.licensenumber?.name)
                    }
                    Prefs.putString(PrefsKey.IMEI, user?.imei)
                    Firebase.crashlytics.setUserId(user?.imei.toString())
                    Prefs.putString(PrefsKey.TIME_INTERVAL, user?.timeInterval.toString())
                    Prefs.putString(PrefsKey.RADIUS, user?.radius.toString())
                    Prefs.putString(PrefsKey.USER_ID, user?.id)
                    val name = user?.firstname + " " + user?.lastname
                    Prefs.putString(PrefsKey.NAME, name)
                    binding.tvUserName.text = name
                    var role = user?.primaryRole.toString()
                    if (role == "null")
                        role = user?.role?.name.toString()
                    binding.tvRole.text = role
                    Prefs.putString(PrefsKey.ROLE, role)
                    pIdLastSelected = user?.property?.id
                    Prefs.putString(PrefsKey.DEF_PROPERTY_ID, pIdLastSelected)
                }
            }
        }
        observeViewState(viewModel.getAllProperties, binding.fetchProgress) { response ->
            if (response != null) {
                if (response?.data == null) {
                    materialDialog(response.errors?.get(0)?.message.toString(), "", "OK") {
                        it.dismiss()
                    }
                } else {
                    proprtyList = response?.data?.getAllProperties
                    val json = proprtyList?.toJson()
                    Prefs.putString(PrefsKey.PROPERTIES, json)
                    setPropertiesSpinner()
                }
            }
        }
        observeViewState(viewModel.getAllMapsByPropertyId, binding.fetchProgress) { response ->
            if (response != null) {
                if (response?.data == null) {
                    materialDialog(response.errors?.get(0)?.message.toString(), "", "OK") {
                        it.dismiss()
                    }
                } else {
                    val proprtyList = response?.data?.getAllMapsByPropertyId
                    if (proprtyList != null) {
                        for (p in proprtyList) {
                            if (p?.isDefault == true) {
                                if (!p.mapTileFile?.filename.isNullOrEmpty()) {
                                    Prefs.putString(
                                        PrefsKey.MAP_TILE_FILE_NAME,
                                        p.mapTileFile?.filename
                                    )
                                    var folder = ""
                                    if (p.mapTileFile?.filename?.contains(".jpg") == true)
                                        folder = p.mapTileFile?.filename?.split(".jpg")?.get(0)
                                    else
                                        folder = p.mapTileFile?.filename?.split(".tiff")?.get(0)
                                            .toString()

                                    Prefs.putString(
                                        PrefsKey.MAP_TILE_FOLDER,
                                        folder
                                    )

                                    //center
                                    Prefs.putDouble(
                                        PrefsKey.MAP_CENTER_LATI,
                                        p.center?.coordinates?.get(1)!!
                                    )
                                    Prefs.putDouble(
                                        PrefsKey.MAP_CENTER_LONGI,
                                        p.center?.coordinates?.get(0)!!
                                    )

                                    //south west
                                    Prefs.putDouble(
                                        PrefsKey.MAP_SOUTHWEST_LATI,
                                        p.center?.coordinates?.get(1)!!
                                    )
                                    Prefs.putDouble(
                                        PrefsKey.MAP_SOUTHWEST_LONGI,
                                        p.center?.coordinates?.get(0)!!
                                    )

                                    //north east
                                    Prefs.putDouble(
                                        PrefsKey.MAP_NORTHEAST_LATI,
                                        p.center?.coordinates?.get(1)!!
                                    )
                                    Prefs.putDouble(
                                        PrefsKey.MAP_NORTHEAST_LONGI,
                                        p.center?.coordinates?.get(0)!!
                                    )
                                    binding.btnStart.visible()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setPropertiesSpinner() {
        if (proprtyList != null) {
            val locationArray = mutableListOf<String>()
            var itemClick = 0
            for ((i, p) in proprtyList!!.withIndex()) {
                locationArray.add(p?.name.toString())
                if (pIdLastSelected == p?.id)
                    itemClick = i
            }
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_spinner, locationArray
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinner.adapter = adapter
            binding.spinner.setSelection(itemClick, false)
        } else {
            materialDialog(getString(R.string.no_property), "", "OK") {
                it.dismiss()
            }
            binding.ivDropdown.gone()
            binding.spinner.gone()
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        try {// TODO: Step 1.6 START create a channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create channel to show notifications.
                val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    // TODO: Step 2.4 change importance
                    NotificationManager.IMPORTANCE_HIGH
                )
                    // TODO: Step 2.6 disable badges for this channel
                    .apply {
                        setShowBadge(false)
                    }

                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationChannel.description = getString(R.string.app_notification_channel_id)

                val notificationManager = requireActivity().getSystemService(
                    NotificationManager::class.java
                )

                notificationManager.createNotificationChannel(notificationChannel)

            }
            // TODO: Step 1.6 END create channel
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun itemClickListener(homeListItem: HomeListItem) {
        try {
            when (homeListItem.title) {
                //            "MAP" -> navController.navigate(R.id.map)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}