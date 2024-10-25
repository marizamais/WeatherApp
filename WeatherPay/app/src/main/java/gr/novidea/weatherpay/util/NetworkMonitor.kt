package gr.novidea.weatherpay.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import gr.novidea.weatherpay.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class NetworkMonitor @Inject constructor(context: Context) {

    private val pingUrl = "8.8.8.8" // Google's public DNS

    private val backgroundThread = HandlerThread("BackgroundThread").apply { start() }
    private lateinit var latencyRunnable: Runnable
    val handler = Handler(backgroundThread.looper)

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(false)
    val isConnected: Flow<Boolean> = _isConnected

    private val _connectionType = MutableStateFlow("-")
    val connectionType: Flow<String> = _connectionType

    private val _latency = MutableStateFlow(0.0)
    val latency: Flow<Double> = _latency

    private val _averageLatency = MutableStateFlow(0.0)
    val averageLatency: Flow<Double> = _averageLatency

    private val networkCb = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isConnected.value = true

            val activeNetwork =
                connectivityManager.getNetworkCapabilities(network) ?: return // null check

            _connectionType.value = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> context.resources.getString(
                    R.string.wifi
                )

                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> context.resources.getString(
                    R.string.cellular
                )

                else -> "Other"
            }

            // Start the handler to update latency every second
            latencyRunnable = object : Runnable {
                override fun run() {
                    val pingTimes = ping(pingUrl, 10)

                    _latency.value =
                        pingTimes.lastOrNull()?.toDouble()?.roundToDecimalPlaces(2) ?: 0.00
                    _averageLatency.value = averageLatency(pingTimes)
                    handler.postDelayed(this, 1000) // Update every second
                }
            }

            handler.post(latencyRunnable)
        }

        override fun onLost(network: Network) {
            _isConnected.value = false
            handler.removeCallbacks(latencyRunnable)
        }
    }

    init {
        // Observe network connectivity changes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCb)
        } else {
            // For API levels below 24
            val networkRequest =
                NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

            connectivityManager.registerNetworkCallback(networkRequest, networkCb)
        }
    }

    /**
     * Ping a URL and return the response time in milliseconds
     *
     */
    private fun ping(url: String, count: Int = 4): List<Long> {
        val command = "/system/bin/ping -c $count $url"
        val process = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        val times = mutableListOf<Long>()
        reader.useLines { lines ->
            lines.forEach { line ->
                val matcher = Regex("time=(\\d+\\.\\d+) ms").find(line)
                matcher?.groupValues?.get(1)?.toDoubleOrNull()?.let {
                    times.add(it.toLong())
                }
            }
        }

        process.waitFor()
        return times
    }

    private fun averageLatency(times: List<Long>): Double {
        return if (times.isNotEmpty()) {
            times.average().roundToDecimalPlaces(2)
        } else {
            0.00
        }
    }

}