package app.brainpool.nodesmobile

import android.app.Application
import android.content.ContextWrapper
import app.brainpool.nodesmobile.util.WifiService
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NodesMobileApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
        setupServices()
    }

    private fun setupServices() {
        WifiService.instance.initializeWithApplicationContext(this)
    }

//    override fun attachBaseContext(base: Context) {
//        if (Prefs.getString(PrefsKey.NIGHT_MODE, "") == "")
//            Prefs.putString(PrefsKey.NIGHT_MODE, getString(R.string.auto))
//        setNightModeOnOff(Prefs.getString(PrefsKey.NIGHT_MODE))
//
//        val context: Context = setupTheme(base,Prefs.getString(PrefsKey.NIGHT_MODE))
//        super.attachBaseContext(context)
//    }
}