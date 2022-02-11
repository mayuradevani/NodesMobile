package app.brainpool.nodesmobile.util

import android.content.Context
import android.net.ConnectivityManager

fun Context.isWifiNetworkConnected(): Boolean {
    var isWifiConn = false
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val allNetworks = manager?.allNetworks?.let { it } ?: return false
    allNetworks.forEach { network ->
        manager.getNetworkInfo(network)?.apply {
            if (type == ConnectivityManager.TYPE_WIFI) {
                isWifiConn = isWifiConn or isConnected
            }
        }
    }
    return isWifiConn
}