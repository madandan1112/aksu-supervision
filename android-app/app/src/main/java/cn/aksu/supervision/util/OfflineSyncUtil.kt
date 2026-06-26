package cn.aksu.supervision.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import cn.aksu.supervision.data.repository.InspectionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 离线数据同步工具
 * 监听网络状态，联网后自动同步离线数据
 */
@Singleton
class OfflineSyncUtil @Inject constructor(
    @ApplicationContext private val context: Context,
    private val inspectionRepository: InspectionRepository
) {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime = _lastSyncTime.asStateFlow()

    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    /** 开始监听网络状态 */
    fun startMonitoring() {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // 网络恢复，触发同步
                syncScope.launch { syncOfflineData() }
            }
        }
        cm.registerNetworkCallback(request, networkCallback!!)

        // 如果当前有网络，立即尝试同步
        if (isNetworkAvailable()) {
            syncScope.launch { syncOfflineData() }
        }
    }

    /** 停止监听 */
    fun stopMonitoring() {
        networkCallback?.let {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            cm.unregisterNetworkCallback(it)
        }
    }

    /** 手动触发同步 */
    suspend fun syncOfflineData(): Result<Int> {
        if (_isSyncing.value) return Result.success(0)
        _isSyncing.value = true
        return try {
            val result = inspectionRepository.syncOfflineData()
            _lastSyncTime.value = System.currentTimeMillis()
            result
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isSyncing.value = false
        }
    }

    /** 检查网络是否可用 */
    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
