package app.brainpool.nodesmobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.get
import androidx.navigation.ui.setupWithNavController
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.MainBinding
import app.brainpool.nodesmobile.util.GlobalVar
import app.brainpool.nodesmobile.util.GlobalVar.TAG
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.observeViewState
import app.brainpool.nodesmobile.view.ui.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: MainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
            binding = MainBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)

            if (!intent.getStringExtra(GlobalVar.EXTRA_FILE_NAME).isNullOrEmpty()) {
                Prefs.putBoolean(PrefsKey.UPDATE_MAP, true)
                Prefs.putString(
                    PrefsKey.MAP_TILE_FILE_NAME,
                    intent.getStringExtra(GlobalVar.EXTRA_FILE_NAME)
                )
            }

            if (!Prefs.getBoolean(PrefsKey.SENT_TOKEN, false))
                sendTokenToServer(this, Prefs.getString(PrefsKey.FIREBASE_TOKEN, ""))

            binding.ivSetting.setOnClickListener {
                if (!isDesiredDestination())
                    navController.navigate(R.id.settingsFragment)
                else
                    navController.popBackStack()
            }
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            binding.bottomNavigation.setupWithNavController(navController)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        observeLiveData()
    }

    private fun isDesiredDestination(): Boolean {
        return with(navController) {
            currentDestination == graph[R.id.settingsFragment]
        }
    }

    private fun observeLiveData() {
        observeViewState(viewModel.main, binding.fetchProgress) { response ->
            if (response != null) {
                if (response.data != null) {
                    Prefs.putBoolean(PrefsKey.SENT_TOKEN, true)
                    Log.v(TAG, "Token sent:" + response.data)
                }
                binding.fetchProgress.gone()
            }
        }
    }

    companion object {
        @ExperimentalCoroutinesApi
        lateinit var viewModel: MainViewModel
        fun sendTokenToServer(context: Context, s: String) {
            if (this::viewModel.isInitialized)
                viewModel.sendToken(context, s)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}