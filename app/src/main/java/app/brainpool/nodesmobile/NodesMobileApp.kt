package app.brainpool.nodesmobile

import android.app.Application
import android.content.ContextWrapper
import app.brainpool.nodesmobile.util.WifiService
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration

@HiltAndroidApp
class NodesMobileApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()
            setupServices()

            // Ready our SDK
            Realm.init(applicationContext)
            // Creating our db with custom properties
            val config = RealmConfiguration.Builder()
                .name("NodesMobile.realm")
                .allowQueriesOnUiThread(true)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
            Realm.setDefaultConfiguration(config)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupServices() {
        WifiService.instance.initializeWithApplicationContext(this)
    }
}