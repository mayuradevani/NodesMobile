package com.example.android.eggtimernotifications.util

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.models.AppNotification
import app.brainpool.nodesmobile.util.fromJson
import app.brainpool.nodesmobile.view.ui.MainActivity
import com.google.gson.Gson
import org.json.JSONObject

private val NOTIFICATION_ID = 0
private var json: JSONObject? = null
fun NotificationManager.sendNotification(
    title: String,
    body: String,
    data: MutableMap<String, String>,
    applicationContext: Context
) {

    json = JSONObject(data as Map<*, *>)
    val appNotification = json.toString().fromJson<AppNotification>()

    try {//    val contentIntent = Intent(applicationContext, MainActivity::class.java)
//    contentIntent.putExtra(GlobalVar.EXTRA_FILE_NAME, fileName)
//    contentIntent.putExtra(GlobalVar.PROPERTY_ID, property_id)
//    val contentPendingIntent = PendingIntent.getActivity(
//        applicationContext,
//        NOTIFICATION_ID,
//        contentIntent,
//        PendingIntent.FLAG_UPDATE_CURRENT
//    )
//    var contentPendingIntent: PendingIntent? = null
//    contentPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_MUTABLE)
//    } else {
//        PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_ONE_SHOT)
//    }
//    val bundle = Bundle()
//    bundle.putString(GlobalVar.EXTRA_FILE_NAME, fileName)
//    bundle.putString(GlobalVar.PROPERTY_ID, property_id)
        val pushNotification = try {
            Gson().fromJson(data.toString(), AppNotification::class.java)
        } catch (e: Exception) {
            null
        }
        val arguments = Bundle()
        arguments.putString("appnotification", data.toString())
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_main)
            .setDestination(
                getNavigationIdForNotificationId(
                    pushNotification?.channelId ?: "0"
                )
            )
            .setArguments(arguments)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.app_notification_channel_id)
        )
            .setSmallIcon(R.drawable.logo_small)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(ContextCompat.getColor(applicationContext, R.color.orange))
            .setAutoCancel(true)
    //        .setContentIntent(
    //            TaskStackBuilder.create(applicationContext).run {
    //                addNextIntentWithParentStack(Intent(applicationContext,MainActivity::class.java).apply {
    //                    putExtras(arguments)
    //                })
    //                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    //            }
    //        )
            .setContentIntent(pendingIntent)
            .setOngoing(false)
        notify(NOTIFICATION_ID, builder.build())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}

fun getNavigationIdForNotificationId(id: String): Int {
    return when (id) {
        "new-map-notification" -> R.id.mapFragment
        else -> R.id.mapFragment

    }
}
