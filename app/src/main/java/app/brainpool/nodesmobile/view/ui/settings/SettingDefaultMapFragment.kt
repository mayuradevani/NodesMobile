package app.brainpool.nodesmobile.view.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.SettingDefaultMapFragmentBinding
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.visible
import com.pixplicity.easyprefs.library.Prefs

class SettingDefaultMapFragment : Fragment(R.layout.setting_default_map_fragment) {

    val strNewest = context?.getString(R.string.newest_txt)
    lateinit var binding: SettingDefaultMapFragmentBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingDefaultMapFragmentBinding.inflate(inflater)
        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        setCheck(Prefs.getString(PrefsKey.DEFAULT_MAP, getString(R.string.newest)))
        binding.tvNewest.setOnClickListener {
            try {
                Prefs.putString(PrefsKey.DEFAULT_MAP, getString(R.string.newest))
                Prefs.putString(PrefsKey.DEFAULT_MAP_STRING, getString(R.string.newest_txt))
                setCheck(getString(R.string.newest))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.tvLastUsed.setOnClickListener {
            try {
                Prefs.putString(PrefsKey.DEFAULT_MAP, getString(R.string.lastUsed))
                Prefs.putString(PrefsKey.DEFAULT_MAP_STRING, getString(R.string.lastUsed_txt))
                setCheck(getString(R.string.lastUsed))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.tvGoogleMaps.setOnClickListener {
            try {
                Prefs.putString(PrefsKey.DEFAULT_MAP, getString(R.string.google_maps_tit))
                Prefs.putString(PrefsKey.DEFAULT_MAP_STRING, getString(R.string.google_maps_txt))
                setCheck(getString(R.string.google_maps_tit))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return binding.root
    }

    private fun setCheck(s: String) {
        if (s == getString(R.string.newest)) {
            binding.ivNewest.visible()
            binding.ivLastUsed.gone()
            binding.ivGoogleMaps.gone()
        } else if (s == getString(R.string.lastUsed)) {
            binding.ivNewest.gone()
            binding.ivLastUsed.visible()
            binding.ivGoogleMaps.gone()
        } else {
            binding.ivNewest.gone()
            binding.ivLastUsed.gone()
            binding.ivGoogleMaps.visible()
        }
        binding.tvModeText.setText(
            Prefs.getString(
                PrefsKey.DEFAULT_MAP_STRING,
                strNewest
            )
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
    }

}