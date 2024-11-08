package com.sparkmembership.sparkowner.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object InternetManager {

    private var applicationContext: Context? = null

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    // Checks if the device is connected to a Wi-Fi network.
    fun isWifiConnected(): Boolean {
        val connectivityManager = getConnectivityManager() ?: return false

        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    suspend fun isInternetWorking(): Boolean {
        return withContext(Dispatchers.IO) {  // Run on IO thread
            try {
                val url = URL("https://www.google.com")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 3000
                connection.connect()
                connection.responseCode == 200
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }


    private fun getConnectivityManager(): ConnectivityManager? {
        return applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    }
}
