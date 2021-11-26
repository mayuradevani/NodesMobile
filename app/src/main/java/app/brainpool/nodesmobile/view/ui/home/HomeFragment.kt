package app.brainpool.nodesmobile.view.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import app.brainpool.nodesmobile.MainActivity
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.Splash
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.HomeFragmentBinding
import app.brainpool.nodesmobile.model.HomeListItem
import app.brainpool.nodesmobile.view.ui.home.adapter.HomeListAdapter
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import dubai.business.womencouncil.data.dataSource.DataServer

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {

    lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: HomeViewModel

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
            viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
            binding.tvThemeChange.setOnClickListener {
                val isNightTheme =
                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                when (isNightTheme) {
                    Configuration.UI_MODE_NIGHT_YES ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    Configuration.UI_MODE_NIGHT_NO ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            binding.tvLogout.setOnClickListener {
                Prefs.putString(PrefsKey.AUTH_KEY, "")
                val intent = Intent(activity, Splash::class.java)
                startActivity(intent)
                activity?.finish()
            }

            binding.ivMap.setOnClickListener {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            binding.recyclerView.apply {
                hasFixedSize()
                layoutManager = GridLayoutManager(context, 2)
                adapter = HomeListAdapter(DataServer.getHomeData())
            }

            val locationArray = resources.getStringArray(R.array.location)
            val adapter = ArrayAdapter(
                context!!,
                R.layout.item_spinner, locationArray
            )
            binding.spinner.adapter = adapter
            binding.spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
            return binding.root
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return container
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
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