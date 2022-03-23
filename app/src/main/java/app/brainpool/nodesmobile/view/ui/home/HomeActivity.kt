package app.brainpool.nodesmobile.view.ui.home

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.HomeBinding
import app.brainpool.nodesmobile.util.setNightModeOnOff
import app.brainpool.nodesmobile.util.setupTheme
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import io.realm.Realm
import io.realm.RealmConfiguration

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var binding: HomeBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Ready our SDK
        Realm.init(this)
        // Creating our db with custom properties
        val config = RealmConfiguration.Builder()
            .name("NodesMobile.realm")
            .allowQueriesOnUiThread(true)
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    override fun attachBaseContext(base: Context) {
        if (base != null) {
            if (Prefs.getString(PrefsKey.NIGHT_MODE, "") == "")
                Prefs.putString(PrefsKey.NIGHT_MODE, base.getString(R.string.auto))
            setNightModeOnOff(base,Prefs.getString(PrefsKey.NIGHT_MODE))

            val context: Context = setupTheme(base, Prefs.getString(PrefsKey.NIGHT_MODE))
            super.attachBaseContext(context)
        }
    }
}