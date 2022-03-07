package app.brainpool.nodesmobile.view.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.util.navigate
import app.brainpool.nodesmobile.view.ui.home.HomeActivity
import app.brainpool.nodesmobile.view.ui.login.LoginActivity
import com.pixplicity.easyprefs.library.Prefs

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        Handler(Looper.getMainLooper()).postDelayed({
            if (Prefs.getString(PrefsKey.AUTH_KEY, "").isNullOrEmpty()) {
                navigate<LoginActivity>()
            } else {
                navigate<HomeActivity>()
            }
            finish()
        }, 3000)
    }
}
