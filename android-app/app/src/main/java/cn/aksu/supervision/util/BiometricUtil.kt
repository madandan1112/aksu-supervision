package cn.aksu.supervision.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * 指纹/人脸识别工具
 * 封装 BiometricPrompt 的调用
 */
object BiometricUtil {

    /** 检查设备是否支持生物识别 */
    fun canAuthenticate(context: Context): Boolean {
        val manager = BiometricManager.from(context)
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    /** 获取不可用原因 */
    fun getUnavailableReason(context: Context): String {
        val manager = BiometricManager.from(context)
        return when (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> "可用"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "设备不支持生物识别"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "生物识别硬件不可用"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "未录入生物识别信息"
            else -> "未知错误"
        }
    }

    /**
     * 发起生物识别认证
     * @param activity FragmentActivity
     * @param title 提示标题
     * @param subtitle 提示副标题
     * @param onSuccess 认证成功回调
     * @param onFailure 认证失败回调
     * @param onError 认证错误回调
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String = "身份验证",
        subtitle: String = "请使用指纹或面容验证身份",
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailure()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("取消")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
