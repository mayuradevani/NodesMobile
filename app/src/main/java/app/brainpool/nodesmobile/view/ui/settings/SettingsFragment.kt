package app.brainpool.nodesmobile.view.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.SettingsFragmentBinding
import com.pixplicity.easyprefs.library.Prefs

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    lateinit var binding: SettingsFragmentBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsFragmentBinding.inflate(inflater)
        binding.tvAboutNodesGo.setOnClickListener { loadAboutUs() }
        binding.tvAboutNodes.setOnClickListener {
            loadAboutUs()
        }

        binding.tvIMEITitle.setOnClickListener { loadIMEIPage() }
        binding.tvIMEITitleGo.setOnClickListener {
            loadIMEIPage()
        }

        binding.tvHelp.setOnClickListener { loadHelp() }
        binding.switchDeviceTracking.setOnCheckedChangeListener(null)
        binding.switchDeviceTracking.isChecked = Prefs.getBoolean(PrefsKey.DEVICE_TRACKING)
        binding.switchDeviceTracking.setOnCheckedChangeListener { buttonView, isChecked ->
            Prefs.putBoolean(PrefsKey.DEVICE_TRACKING, isChecked)
        }

//        binding.tvNightModeVal.text = Prefs.getString(PrefsKey.NIGHT_MODE, getString(R.string.auto))
//        binding.tvNightMode.setOnClickListener {
//            loadNightMode()
//        }
//        binding.tvNightModeVal.setOnClickListener {
//            loadNightMode()
//        }
//        binding.tvUnits.setOnClickListener {
//            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSettingUnitsFragment())
//        }
        binding.tvDataUsage.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSettingDataUsageFragment())
        }
//        binding.tvDefaultMap.setOnClickListener {
//            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSettingDefaultMapFragment())
//        }
        return binding.root
    }

    private fun loadNightMode() {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSettingNightModeFragment())
    }

    private fun loadAboutUs() {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAboutFragment())
    }

    private fun loadIMEIPage() {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToIMEIFragment())
    }

    private fun loadHelp() {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToHelpFragment())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
    }

}