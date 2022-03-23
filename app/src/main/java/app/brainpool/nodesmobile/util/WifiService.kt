package app.brainpool.nodesmobile.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.util.Log
import app.brainpool.nodesmobile.util.GlobalVar.TAG

class WifiService {
    private lateinit var wifiManager: WifiManager
    private lateinit var connectivityManager: ConnectivityManager

    companion object {
        val instance = WifiService()
    }

    fun initializeWithApplicationContext(context: Context) {
        wifiManager = context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun isOnline(): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i(TAG, "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i(TAG, "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i(TAG, "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
}