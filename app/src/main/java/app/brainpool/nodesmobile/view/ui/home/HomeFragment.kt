package app.brainpool.nodesmobile.view.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import app.brainpool.nodesmobile.MainActivity
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.Splash
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.HomeFragmentBinding
import app.brainpool.nodesmobile.data.models.HomeListItem
import app.brainpool.nodesmobile.util.materialDialog
import app.brainpool.nodesmobile.util.navigate
import app.brainpool.nodesmobile.util.navigateClearStack
import app.brainpool.nodesmobile.util.observeViewState
import app.brainpool.nodesmobile.view.ui.home.adapter.HomeListAdapter
import com.google.firebase.messaging.FirebaseMessaging
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import dubai.business.womencouncil.data.dataSource.DataServer


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {

    lateinit var binding: HomeFragmentBinding
    private val viewModel by viewModels<HomeViewModel>()

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
            binding.tvLogout.setOnClickListener {
                viewModel.logout(requireContext())
            }

            binding.ivMap.setOnClickListener {
                goToMain()
            }

            binding.recyclerView.apply {
                hasFixedSize()
                layoutManager = GridLayoutManager(context, 2)
                adapter = HomeListAdapter(DataServer.getHomeData())
            }

            val locationArray = resources.getStringArray(R.array.location)
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_spinner, locationArray
            )
            binding.spinner.adapter = adapter
//            binding.spinner.onItemSelectedListener = object :
//                AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>,
//                    view: View, position: Int, id: Long
//                ) {
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>) {
//                }
//            }
            viewModel.getUserProfile(requireContext())
            observeLiveData()
            return binding.root
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return container
    }

    private fun goToMain() {
        activity?.navigate<MainActivity>()
    }

    private fun observeLiveData() {
        observeViewState(viewModel.userProfile) { response ->
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
                    Prefs.putString(PrefsKey.TIME_INTERVAL, user?.timeInterval.toString())
                    Prefs.putString(PrefsKey.RADIUS, user?.radius.toString())
                    if (!user?.property?.id.isNullOrEmpty()) {
                        Prefs.putString(PrefsKey.PROPERTY_ID, user?.property?.id)
                        Prefs.putString(PrefsKey.USER_ID, user?.id)
                        viewModel.getAllMapsByPropertyId(
                            requireContext(),
                            user?.property?.id.toString()
                        )
                    }
                }
            }
        }
        observeViewState(viewModel.logout) { response ->
            if (response?.data?.logoutUserData?.success == true) {
                try {
                    FirebaseMessaging.getInstance().deleteToken()
                    Prefs.clear()
                    activity?.finish()
                    activity?.navigateClearStack<Splash>()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        observeViewState(viewModel.getAllMapsByPropertyId) { response ->
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
                                    Prefs.putString(
                                        PrefsKey.MAP_TILE_FOLDER,
                                        p.mapTileFile?.filename?.split(".jpg")?.get(0)
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

                                }
                            }
                        }
                    }
                }
            }
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