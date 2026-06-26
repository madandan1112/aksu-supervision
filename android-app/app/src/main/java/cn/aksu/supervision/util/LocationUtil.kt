package cn.aksu.supervision.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlin.coroutines.resume

/**
 * 位置获取工具
 * 获取当前 GPS 坐标及逆地理编码地址
 */
object LocationUtil {

    private var fusedClient: FusedLocationProviderClient? = null

    /** 初始化（在 Application.onCreate 中调用） */
    fun init(context: Context) {
        fusedClient = LocationServices.getFusedLocationProviderClient(context)
    }

    data class LocationResult(
        val latitude: Double,
        val longitude: Double,
        val address: String?
    )

    /**
     * 获取当前位置
     * 优先获取精确位置，失败则使用最后已知位置
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Result<LocationResult> {
        return try {
            val client = fusedClient ?: LocationServices.getFusedLocationProviderClient(context)
            val cts = CancellationTokenSource()

            val location = try {
                client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token).await()
            } catch (_: Exception) {
                // 精确定位失败，尝试获取最后已知位置
                client.lastLocation.await()
            }

            if (location != null) {
                val address = reverseGeocode(context, location.latitude, location.longitude)
                Result.success(
                    LocationResult(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        address = address
                    )
                )
            } else {
                Result.failure(Exception("无法获取位置信息"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 逆地理编码：坐标 → 地址
     */
    private fun reverseGeocode(context: Context, lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.CHINA)
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            addresses?.firstOrNull()?.getAddressLine(0)
        } catch (_: Exception) {
            null
        }
    }
}
