package app.brainpool.nodesmobile.view.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.SettingDataUsageFragmentBinding
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.visible
import com.pixplicity.easyprefs.library.Prefs

class SettingDataUsageFragment : Fragment(R.layout.setting_data_usage_fragment) {

    val strAuto = context?.getString(R.string.always_text)
    lateinit var binding: SettingDataUsageFragmentBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingDataUsageFragmentBinding.inflate(inflater)
        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        setCheck(Prefs.getString(PrefsKey.DATA_USAGE, "Always"))
        binding.tvAlways.setOnClickListener {
            try {
                Prefs.putString(PrefsKey.DATA_USAGE, getString(R.string.always))
                Prefs.putString(PrefsKey.DATA_USAGE_STRING, getString(R.string.always_text))
                setCheck(getString(R.string.always))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.tvWifiOnly.setOnClickListener {
            try {
                Prefs.putString(PrefsKey.DATA_USAGE, getString(R.string.wifiOnly))
                Prefs.putString(PrefsKey.DATA_USAGE_STRING, getString(R.string.wifi_only_text))
                setCheck(getString(R.string.wifiOnly))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return binding.root
    }

    private fun setCheck(s: String) {
        if (s == getString(R.string.always)) {
            binding.ivAlways.visible()
            binding.ivWifiOnly.gone()
        } else {
            binding.ivAlways.gone()
            binding.ivWifiOnly.visible()
        }
        binding.tvModeText.setText(
            Prefs.getString(
                PrefsKey.DATA_USAGE_STRING,
                strAuto
            )
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
    }

}