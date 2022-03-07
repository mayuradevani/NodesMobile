package app.brainpool.nodesmobile.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

class WifiService {
    private lateinit var wifiManager: WifiManager
    private lateinit var connectivityManager: ConnectivityManager

    companion object {
        val instance = WifiService()
    }

    fun initializeWithApplicationContext(context: Context) {
        wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun isOnline(): Boolean {

        return false
    }
}