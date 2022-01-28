package app.brainpool.nodesmobile.view.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.SettingUnitsFragmentBinding
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.visible
import com.pixplicity.easyprefs.library.Prefs

class SettingUnitsFragment : Fragment(R.layout.setting_units_fragment) {

    lateinit var binding: SettingUnitsFragmentBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingUnitsFragmentBinding.inflate(inflater)
        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        setCheck(Prefs.getString(PrefsKey.UNITS, getString(R.string.auto)))
        binding.tvMetric.setOnClickListener {
            try {
                Prefs.putString(PrefsKey.UNITS, getString(R.string.metric))
                setCheck(getString(R.string.metric))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.tvImperial.setOnClickListener {
            try {
                Prefs.putString(PrefsKey.UNITS, getString(R.string.imperial))
                setCheck(getString(R.string.imperial))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.tvNautical.setOnClickListener {
            try {
                Prefs.putString(PrefsKey.UNITS, getString(R.string.nautical))
                setCheck(getString(R.string.nautical))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return binding.root
    }

    private fun setCheck(s: String) {
        if (s == getString(R.string.imperial)) {
            binding.ivImperial.visible()
            binding.ivMetric.gone()
            binding.ivNautical.gone()
        } else if (s == getString(R.string.metric)) {
            binding.ivImperial.gone()
            binding.ivMetric.visible()
            binding.ivNautical.gone()
        } else {
            binding.ivImperial.gone()
            binding.ivMetric.gone()
            binding.ivNautical.visible()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
    }

}