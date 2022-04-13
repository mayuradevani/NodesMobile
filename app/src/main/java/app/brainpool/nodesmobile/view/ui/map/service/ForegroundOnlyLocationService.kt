package app.brainpool.nodesmobile.view.ui.map.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.util.GlobalVar.TAG
import com.google.android.gms.location.*
import com.pixplicity.easyprefs.library.Prefs

/**
 * Service tracks location when requested and updates Activity via binding. If Activity is
 * stopped/unbinds and tracking is enabled, the service promotes itself to a foreground service to
 * insure location updates aren't interrupted.
 *
 * For apps running in the background on O+ devices, location is computed much less than previous
 * versions. Please reference documentation for details.
 */
class ForegroundOnlyLocationService : Service() {
    /*
     * Checks whether the bound activity has really gone away (foreground service with notification
     * created) or simply orientation change (no-op).
     */
    private var configurationChange = false

    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()

    private lateinit var notificationManager: NotificationManager

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    override fun onCreate() {
        Log.d(TAG, "onCreate()")

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // TODO: Step 1.2, Review the FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = (1000 * Prefs.getString(PrefsKey.TIME_INTERVAL).toLong())
            fastestInterval = (1000 * Prefs.getString(PrefsKey.TIME_INTERVAL).toLong())
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // TODO: Step 1.4, Initialize the LocationCallback.
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // Normally, you want to save a new location to a database. We are simplifying
                // things a bit and just saving it as a local variable, as we only need it again
                // if a Notification is created (when the user navigates away from app).
                currentLocation = locationResult.lastLocation
                handleLocationResult(currentLocation)
            }
        }
    }

    private fun handleLocationResult(locationResult: Location?) {

        // Notify our Activity that a new location was added. Again, if this was a
        // production app, the Activity would be listening for changes to a database
        // with new locations, but we are simplifying things a bit to focus on just
        // learning the location side of things.
        val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, locationResult)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        // Updates notification content if this service is running as a foreground
        // service.
//        if (serviceRunningInForeground) {
//            notificationManager.notify(
//                NOTIFICATION_ID,
//                generateNotification(locationResult)
//            )
//        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")

        val cancelLocationTrackingFromNotification =
            intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)

        if (cancelLocationTrackingFromNotification) {
            unsubscribeToLocationUpdates()
            stopSelf()
        }
        // Tells the system not to recreate the service after it's been killed.
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind()")

        // MainActivity (client) comes into foreground and binds to service, so the service can
        // become a background services.
//        stopForeground(true)
//        serviceRunningInForeground = false
//        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        Log.d(TAG, "onRebind()")

        // MainActivity (client) returns to the foreground and rebinds to service, so the service
        // can become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind()")

        // MainActivity (client) leaves foreground, so service needs to become a foreground service
        // to maintain the 'while-in-use' label.
        // NOTE: If this method is called due to a configuration change in MainActivity,
        // we do nothing.
        if (!configurationChange && Prefs.getBoolean(PrefsKey.KEY_FOREGROUND_ENABLED, false)) {
            Log.d(TAG, "Start foreground service")
//            val notification = generateNotification(currentLocation)
//            startForeground(NOTIFICATION_ID, notification)
//            serviceRunningInForeground = true
        }

        // Ensures onRebind() is called if MainActivity (client) rebinds.
        return true
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    fun subscribeToLocationUpdates() {
        Log.d(TAG, "subscribeToLocationUpdates()")
        Prefs.putBoolean(PrefsKey.KEY_FOREGROUND_ENABLED, true)

        // Binding to this service doesn't actually trigger onStartCommand(). That is needed to
        // ensure this Service can be promoted to a foreground service, i.e., the service needs to
        // be officially started (which we do here).
        startService(Intent(applicationContext, ForegroundOnlyLocationService::class.java))

        try {
            // TODO: Step 1.5, Subscribe to location changes.
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            Prefs.putBoolean(PrefsKey.KEY_FOREGROUND_ENABLED, false)
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun unsubscribeToLocationUpdates() {
        Log.d(TAG, "unsubscribeToLocationUpdates()")

        try {
            // TODO: Step 1.6, Unsubscribe to location changes.
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Location Callback removed.")
                    stopSelf()
                } else {
                    Log.d(TAG, "Failed to remove Location Callback.")
                }
            }
            Prefs.putBoolean(PrefsKey.KEY_FOREGROUND_ENABLED, false)
        } catch (unlikely: SecurityException) {
            Prefs.putBoolean(PrefsKey.KEY_FOREGROUND_ENABLED, true)
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

//    /*
//     * Generates a BIG_TEXT_STYLE Notification that represent latest location.
//     */
//    private fun generateNotification(location: Location?): Notification {
//        Log.d(TAG, "generateNotification()")
//        val mainNotificationText = location?.toText() ?: getString(R.string.no_location_text)
//        val titleText = getString(R.string.app_name) + " " + getString(R.string.is_using_location)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel = NotificationChannel(
//                NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//        val bigTextStyle = NotificationCompat.BigTextStyle()
//            .bigText(mainNotificationText)
//            .setBigContentTitle(titleText)
//        val launchActivityIntent = Intent(this, MainActivity::class.java)
//        val cancelIntent = Intent(this, ForegroundOnlyLocationService::class.java)
//        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)
//
//        val servicePendingIntent = PendingIntent.getService(
//            this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        val activityPendingIntent = PendingIntent.getActivity(
//            this, 0, launchActivityIntent, 0
//        )
//        val notificationCompatBuilder =
//            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
//
//
//        return notificationCompatBuilder
//            .setStyle(bigTextStyle)
//            .setContentTitle(titleText)
//            .setContentText(mainNotificationText)
//            .setSmallIcon(R.drawable.logo_small)
//            .setColor(ContextCompat.getColor(baseContext, R.color.orange))
//            .setDefaults(NotificationCompat.DEFAULT_ALL)
//            .setOngoing(true)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .addAction(
//                R.drawable.logo_small, getString(R.string.launch_activity),
//                activityPendingIntent
//            )
//            .addAction(
//                R.drawable.logo_small,
//                getString(R.string.stop_location_updates_button_text),
//                servicePendingIntent
//            )
//            .build()
//    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        internal val service: ForegroundOnlyLocationService
            get() = this@ForegroundOnlyLocationService
    }

    companion object {

        private const val PACKAGE_NAME = "com.example.android.whileinuselocation"

        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"

        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"

        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"

        private const val NOTIFICATION_ID = 12345678

        private const val NOTIFICATION_CHANNEL_ID = "while_in_use_channel_01"
    }
}
