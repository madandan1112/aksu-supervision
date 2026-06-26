package cn.aksu.supervision.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RectificationNotice(
    val id: Long,
    val inspectionId: Long,
    val taskId: Long,
    val enterpriseId: Long,
    val enterpriseName: String,
    val inspectorId: Long,
    val inspectorName: String,
    val issues: List<RectificationIssue>,
    val deadline: String,
    val status: String,             // ISSUED / RECTIFYING / SUBMITTED / ACCEPTED / REJECTED / OVERDUE
    val rectificationPhotos: List<String>?,
    val rectificationRemark: String?,
    val acceptanceConclusion: String?,  // PASS / FAIL
    val acceptanceRemark: String?,
    val acceptorId: Long?,
    val acceptorName: String?,
    @SerializedName("accepted_at")
    val acceptedAt: String?,
    val issuedAt: String,
    val updatedAt: String?
) : Serializable {

    fun statusLabel(): String = when (status) {
        "ISSUED" -> "已下达"
        "RECTIFYING" -> "整改中"
        "SUBMITTED" -> "已提交验收"
        "ACCEPTED" -> "验收通过"
        "REJECTED" -> "验收不通过"
        "OVERDUE" -> "已逾期"
        else -> status
    }
}

data class RectificationIssue(
    val id: Long,
    val description: String,
    val standard: String?,
    val evidence: String?,          // 违法证据描述
    val requirement: String?,       // 整改要求
    val category: String?
) : Serializable
