package app.brainpool.nodesmobile.view.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.SettingsFragmentBinding
import app.brainpool.nodesmobile.util.await
import app.brainpool.nodesmobile.util.materialDialog
import app.brainpool.nodesmobile.util.navigateClearStack
import app.brainpool.nodesmobile.view.ui.MainActivity
import app.brainpool.nodesmobile.view.ui.Splash
import app.brainpool.nodesmobile.view.ui.settings.about.AboutFragment
import com.alcophony.app.ui.core.BaseFragment
import com.google.firebase.messaging.FirebaseMessaging
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.settings_fragment) {

    lateinit var binding: SettingsFragmentBinding

    @ExperimentalCoroutinesApi
    private val viewModel by viewModels<SettingsViewModel>()

    @OptIn(ExperimentalCoroutinesApi::class)
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
        binding.switchDeviceTracking.isChecked = Prefs.getBoolean(PrefsKey.DEVICE_TRACKING, true)
        binding.switchDeviceTracking.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.updateLocUpdateStatus(requireContext(), isChecked)
            Prefs.putBoolean(PrefsKey.DEVICE_TRACKING, isChecked)
        }
        binding.tvLogout.setOnClickListener {
            materialDialog(
                getString(R.string.do_you_want_to_logout),
                "",
                getString(R.string.yes),
                {
                    viewModel.logout(requireContext())
                }, getString(R.string.no), { it.dismiss() })
        }
        binding.tvNightModeVal.text = Prefs.getString(PrefsKey.NIGHT_MODE, getString(R.string.auto))
        binding.tvNightMode.setOnClickListener {
            loadNightMode()
        }
        binding.tvNightModeVal.setOnClickListener {
            loadNightMode()
        }
        observeLiveData()
        return binding.root
    }

    private fun observeLiveData() {
        observeViewState(viewModel.logout, binding.fetchProgress) { response ->
            if (response?.data?.logoutUserData?.success == true) {
                try {
                    viewLifecycleOwner
                        .lifecycleScope
                        .launch {
                            FirebaseMessaging.getInstance().deleteToken().await()
                            Prefs.clear()
                            requireActivity().navigateClearStack<Splash>()
                            activity?.finish()
                        }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        observeViewState(viewModel.updateLocUpdateStatus, binding.fetchProgress) { response ->
        }
    }

    private fun loadNightMode() {
        showFragment(SettingNightModeFragment())
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

}