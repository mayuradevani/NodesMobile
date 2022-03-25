package app.brainpool.nodesmobile.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.data.models.AppNotification
import app.brainpool.nodesmobile.view.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pixplicity.easyprefs.library.Prefs
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var json: JSONObject? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage?.from}")
        remoteMessage?.data?.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }
        remoteMessage?.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(
                it.title!!,
                it.body!!,
                remoteMessage.data
            )//remoteMessage.data.get("mapFileName"),remoteMessage.data.get("propertyId")
        }
    }

    private fun sendNotification(
        title: String,
        body: String,
        data: MutableMap<String, String>,
    ) {

        json = JSONObject(data as Map<*, *>)
        val appNotification = json.toString().fromJson<AppNotification>()
        showAppNotification(appNotification,title,body)


//        val notificationManager = ContextCompat.getSystemService(
//            applicationContext,
//            NotificationManager::class.java
//        ) as NotificationManager
//        notificationManager.sendNotification(title, messageBody, data, applicationContext)
    }

    private fun showAppNotification(appNotification: AppNotification?, title: String, body: String) {
        try {
            val arguments = Bundle()
            arguments.putString("appnotification", appNotification?.toJson() ?: "{}")

//            val pendingIntent = NavDeepLinkBuilder(baseContext)
//                .setGraph(R.navigation.nav_main)
//                .setArguments(arguments)
//                .setComponentName(MainActivity::class.java)
//                .setDestination(getNavigationIdForNotificationType(appNotification?.channelId.orEmpty()))
//                .createPendingIntent()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
//            var flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//            flags=PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            val pendingIntent = TaskStackBuilder.create(baseContext).run {
                addNextIntentWithParentStack(Intent(baseContext, MainActivity::class.java).apply {putExtras(arguments)
                })
                getPendingIntent(0,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }

            val builder = NotificationCompat.Builder(
                this,
                applicationContext.getString(R.string.app_notification_channel_id)
            )
                .setSmallIcon(R.drawable.logo_small)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(ContextCompat.getColor(baseContext, R.color.orange))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setOngoing(false)


            builder.build().apply {
                val notificationManager =
                    NotificationManagerCompat.from(this@MyFirebaseMessagingService)
                notificationManager.notify(System.currentTimeMillis().toInt(), this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun NotificationManager.cancelNotifications() {
        cancelAll()
    }


    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        Prefs.putBoolean(PrefsKey.SENT_TOKEN, false)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}