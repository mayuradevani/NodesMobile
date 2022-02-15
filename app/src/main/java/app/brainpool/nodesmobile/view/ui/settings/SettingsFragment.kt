package app.brainpool.nodesmobile.view.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.brainpool.nodesmobile.MainActivity
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
        return binding.root
    }

    private fun loadAboutUs() {
        showFragment(AboutFragment())
    }

    private fun showFragment(frmt: Fragment) {
        (activity as MainActivity).fm.beginTransaction()
            .add(R.id.nav_host_fragment, frmt, "6")
            .hide((activity as MainActivity).active)
            .commit()
        (activity as MainActivity).lastActive = (activity as MainActivity).active
        (activity as MainActivity).active = frmt
    }

    private fun loadIMEIPage() {
        showFragment(IMEIFragment())
    }

    private fun loadHelp() {
        showFragment(HelpFragment())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
    }
}