package app.brainpool.nodesmobile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.view.ui.home.HomeActivity
import app.brainpool.nodesmobile.view.ui.login.LoginActivity
import com.pixplicity.easyprefs.library.Prefs

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        Handler(Looper.getMainLooper()).postDelayed({
            var intent: Intent
            if (Prefs.getString(PrefsKey.AUTH_KEY, "").isNullOrEmpty()) {
                intent = Intent(this, LoginActivity::class.java)
            } else {
                intent = Intent(this, HomeActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}
