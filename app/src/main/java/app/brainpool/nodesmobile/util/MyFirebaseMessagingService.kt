package app.brainpool.nodesmobile.util

import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import app.brainpool.nodesmobile.data.PrefsKey
import com.example.android.eggtimernotifications.util.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pixplicity.easyprefs.library.Prefs

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage?.from}")
        remoteMessage?.data?.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }
        remoteMessage?.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title!!, remoteMessage.data)//remoteMessage.data.get("mapFileName"),remoteMessage.data.get("propertyId")
        }
    }

    private fun sendNotification(
        title: String,
        messageBody: MutableMap<String, String>,
    ) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(title, messageBody,  applicationContext)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        Prefs.putBoolean(PrefsKey.SENT_TOKEN, false)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}