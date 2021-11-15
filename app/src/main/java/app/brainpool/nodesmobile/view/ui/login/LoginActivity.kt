package app.brainpool.nodesmobile.view.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.LoginBinding
import app.brainpool.nodesmobile.view.ui.home.HomeActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var binding: LoginBinding
    lateinit var navController: NavController

    @ExperimentalCoroutinesApi
    lateinit var viewModel: LoginViewModel

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        navController = findNavController(R.id.navLogin)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { link ->
                navController.handleDeepLink(Intent().apply {
                    try {
                        var deepLink: Uri? = null
                        if (link != null) {
                            deepLink = link.link
                            val token = deepLink?.getQueryParameter("token")
                            Prefs.putString(PrefsKey.AUTH_KEY, token)
                            Log.v(TAG, "Key: " + Prefs.getString(PrefsKey.AUTH_KEY, "not rec"))
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            this@LoginActivity.finish()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
}