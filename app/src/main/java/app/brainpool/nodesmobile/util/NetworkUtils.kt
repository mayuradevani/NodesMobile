package app.brainpool.nodesmobile.util

import android.content.Context
import android.net.ConnectivityManager

fun Context.isWifiNetworkConnected(): Boolean {
    var isWifiConn = false
//        var isMobileConn= false
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val allNetworks = manager?.allNetworks?.let { it } ?: return false
    allNetworks.forEach { network ->
        manager.getNetworkInfo(network)?.apply {
            if (type.equals(ConnectivityManager.TYPE_WIFI) == true) {
                isWifiConn = isWifiConn or isConnected
            }
//                if (type.equals(ConnectivityManager.TYPE_MOBILE)) {
//                    isMobileConn = isMobileConn or isConnected
//                }
        }

    }
    return isWifiConn
}