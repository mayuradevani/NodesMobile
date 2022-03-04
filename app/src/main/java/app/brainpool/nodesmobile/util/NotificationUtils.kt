package com.example.android.eggtimernotifications.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import app.brainpool.nodesmobile.MainActivity
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.util.GlobalVar

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0
fun NotificationManager.sendNotification(
    title: String,
    messageBody: String,
    fileName: String?,
    applicationContext: Context
) {


    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    contentIntent.putExtra(GlobalVar.EXTRA_FILE_NAME, fileName)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val GROUP_ID = "group_id"

    val builder = NotificationCompat.Builder(applicationContext,  applicationContext.getString(R.string.app_notification_channel_id))
        .setSmallIcon(R.drawable.logo_small)
        .setContentTitle(title)
        .setContentText(messageBody)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//        .setColor(ContextCompat.getColor(applicationContext, R.color.orange))
        .setGroup(GROUP_ID)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setOngoing(false)
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

//    val builder = NotificationCompat.Builder(
//        applicationContext,
//        applicationContext.getString(R.string.app_notification_channel_id)
//    )
//        .setSmallIcon(R.drawable.logo_brainpool)
//        .setContentTitle(title)
//        .setContentText(messageBody)
//        .setContentIntent(contentPendingIntent)
//        .setStyle(bigPicStyle)
//        .setLargeIcon(eggImage)
//        .addAction(
//            R.drawable.logo_brainpool,
//            applicationContext.getString(R.string.snooze),
//            snoozePendingIntent
//        )
//        .setPriority(NotificationCompat.PRIORITY_HIGH)
//        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
