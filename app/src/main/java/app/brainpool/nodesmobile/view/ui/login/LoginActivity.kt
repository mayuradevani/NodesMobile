package app.brainpool.nodesmobile.view.ui.login

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.LoginBinding
import app.brainpool.nodesmobile.util.navigateClearStack
import app.brainpool.nodesmobile.view.ui.home.HomeActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var binding: LoginBinding
    lateinit var navController: NavController

    @ExperimentalCoroutinesApi
    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        navController = findNavController(R.id.navLogin)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        try {
            if (intent != null)
                FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(intent)
                    .addOnSuccessListener(this) { link ->
                        navController?.handleDeepLink(Intent().apply {
                            try {
                                if (link != null) {
                                    createChannel(
                                        getString(R.string.app_notification_channel_id),
                                        getString(R.string.app_notification_channel_name)
                                    )
                                    var deepLink: Uri? = link.link
                                    val authToken: String =
                                        deepLink?.getQueryParameter("token").toString()
                                    if (!authToken.isNullOrEmpty()) {
                                        var pathFolder =
                                            Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES
                                            )
                                                .toString() + "/.NodesMobile"
                                        File(pathFolder).deleteRecursively()
                                        navController?.navigate(R.id.holdingFragment)
                                        Prefs.putString(PrefsKey.AUTH_KEY, authToken)
                                        val userId: String =
                                            deepLink?.getQueryParameter("userId").toString()
//                                        Prefs.putString(PrefsKey.USER_ID, userId)
                                        Prefs.putString(PrefsKey.MAP_TYPE, getString(R.string.overlay))
                                        Log.v(
                                            ContentValues.TAG,
                                            "Key: " + Prefs.getString(PrefsKey.AUTH_KEY, "not rec")
                                        )
                                        navigateClearStack<HomeActivity>()
                                        this@LoginActivity.finish()
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        })
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
                    .apply {
                        setShowBadge(false)
                    }

                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationChannel.description = getString(R.string.app_notification_channel_id)

                val notificationManager = getSystemService(
                    NotificationManager::class.java
                )

                notificationManager.createNotificationChannel(notificationChannel)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
}