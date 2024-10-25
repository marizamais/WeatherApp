package gr.novidea.weatherpay.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.novidea.weatherpay.util.NetworkMonitor
import javax.inject.Inject

@HiltViewModel
class FlowerViewModel @Inject constructor(private val networkMonitor: NetworkMonitor) :
    ViewModel() {

    val isOnline = networkMonitor.isConnected.asLiveData()
    val connectionType = networkMonitor.connectionType.asLiveData()

    val latency = networkMonitor.latency.asLiveData()
    val averageLatency = networkMonitor.averageLatency.asLiveData()
}