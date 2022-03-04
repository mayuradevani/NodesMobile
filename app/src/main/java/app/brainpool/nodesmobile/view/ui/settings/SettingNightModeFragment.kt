package app.brainpool.nodesmobile.view.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.SettingNightModeFragmentBinding
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.setupTheme
import app.brainpool.nodesmobile.util.visible
import com.alcophony.app.ui.core.BaseFragment
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingNightModeFragment : BaseFragment(R.layout.setting_night_mode_fragment) {

    val strAuto = context?.getString(R.string.automatic_text)
    lateinit var binding: SettingNightModeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingNightModeFragmentBinding.inflate(inflater)
        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        setCheck(Prefs.getString(PrefsKey.NIGHT_MODE, getString(R.string.auto)))
        binding.tvOn.setOnClickListener {
            try {
                if (Prefs.getString(PrefsKey.NIGHT_MODE) != getString(R.string.on)) {
                    Prefs.putString(PrefsKey.NIGHT_MODE, getString(R.string.on))
                    Prefs.putString(PrefsKey.NIGHT_MODE_STRING, getString(R.string.on_text))
                    setCheck("On")
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    context?.let { it1 -> setupTheme(it1, Prefs.getString(PrefsKey.NIGHT_MODE)) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.tvOff.setOnClickListener {
            try {
                if (Prefs.getString(PrefsKey.NIGHT_MODE) != getString(R.string.off)) {
                    Prefs.putString(PrefsKey.NIGHT_MODE, getString(R.string.off))
                    Prefs.putString(
                        PrefsKey.NIGHT_MODE_STRING,
                        getString(R.string.off_text)
                    )
                    setCheck("Off")
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    context?.let { it1 -> setupTheme(it1, Prefs.getString(PrefsKey.NIGHT_MODE)) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.tvAuto.setOnClickListener {
            try {
                if (Prefs.getString(PrefsKey.NIGHT_MODE) != getString(R.string.auto)) {
                    Prefs.putString(PrefsKey.NIGHT_MODE, getString(R.string.auto))
                    Prefs.putString(
                        PrefsKey.NIGHT_MODE_STRING,
                        strAuto
                    )
                    setCheck("Auto")
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    context?.let { it1 -> setupTheme(it1, Prefs.getString(PrefsKey.NIGHT_MODE)) }
                }

//            try {
//                var nightMOde = "Auto"
//                val isNightTheme =
//                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//                when (isNightTheme) {
//                    Configuration.UI_MODE_NIGHT_YES -> {
//                        nightMOde = "On"
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                    }
//                    Configuration.UI_MODE_NIGHT_NO -> {
//                        nightMOde = "Off"
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                    }
//                }
//                Prefs.putString(PrefsKey.NIGHT_MODE, nightMOde)
//                val intent = Intent(activity, MainActivity::class.java)
//                intent.putExtra(PrefsKey.NIGHT_MODE, true)
//                startActivity(intent)
//                activity?.finish()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return binding.root
    }

    private fun setCheck(s: String) {
        if (s == "On") {
            binding.ivOn.visible()
            binding.ivOff.gone()
            binding.ivAuto.gone()
        } else if (s == "Off") {
            binding.ivOn.gone()
            binding.ivOff.visible()
            binding.ivAuto.gone()
        } else {
            binding.ivOn.gone()
            binding.ivOff.gone()
            binding.ivAuto.visible()
        }
        binding.tvModeText.setText(
            Prefs.getString(
                PrefsKey.NIGHT_MODE_STRING,
                strAuto
            )
        )
    }

}