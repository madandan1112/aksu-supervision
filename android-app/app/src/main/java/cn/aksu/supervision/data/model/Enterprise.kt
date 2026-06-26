package cn.aksu.supervision.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Enterprise(
    val id: Long,
    val name: String,
    val unifiedCode: String,        // 统一社会信用代码
    val legalPerson: String?,
    val phone: String?,
    val address: String?,
    val longitude: Double?,
    val latitude: Double?,
    val industry: String?,
    val category: String?,          // 行业类别
    val riskLevel: String?,         // LOW / MEDIUM / HIGH
    val supervisionType: String?,   // 重点/一般
    val status: String?,            // NORMAL / SUSPENDED / REVOKED
    val lastInspectionDate: String?,
    val nextInspectionDate: String?,
    val inspectionCount: Int = 0,
    val rectificationCount: Int = 0,
    @SerializedName("qr_code")
    val qrCode: String?,
    val createdAt: String,
    val updatedAt: String?
) : Serializable {

    fun riskLevelLabel(): String = when (riskLevel) {
        "LOW" -> "低风险"
        "MEDIUM" -> "中风险"
        "HIGH" -> "高风险"
        else -> "未评定"
    }

    fun statusLabel(): String = when (status) {
        "NORMAL" -> "正常"
        "SUSPENDED" -> "停业"
        "REVOKED" -> "注销"
        else -> "未知"
    }
}
