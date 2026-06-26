package cn.aksu.supervision.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InspectionRecord(
    val id: Long,
    val taskId: Long,
    val enterpriseId: Long,
    val enterpriseName: String,
    val inspectorId: Long,
    val inspectorName: String,
    val inspectionDate: String,
    val items: List<InspectionItem>,
    val photos: List<InspectionPhoto>?,
    val conclusion: String,        // QUALIFIED / UNQUALIFIED / CONDITIONAL
    val remark: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val status: String,            // DRAFT / SUBMITTED / REVIEWED
    val createdAt: String,
    val updatedAt: String?
) : Serializable {

    fun conclusionLabel(): String = when (conclusion) {
        "QUALIFIED" -> "合格"
        "UNQUALIFIED" -> "不合格"
        "CONDITIONAL" -> "有条件合格"
        else -> conclusion
    }
}

data class InspectionItem(
    val id: Long,
    val category: String,
    val content: String,
    val standard: String?,
    val result: String?,           // PASS / FAIL / N/A
    val remark: String?
) : Serializable {

    fun resultLabel(): String = when (result) {
        "PASS" -> "符合"
        "FAIL" -> "不符合"
        "N/A" -> "不适用"
        else -> "未检查"
    }
}

data class InspectionPhoto(
    val id: Long,
    val path: String,
    @SerializedName("thumbnail_path")
    val thumbnailPath: String?,
    val description: String?,
    val latitude: Double?,
    val longitude: Double?,
    @SerializedName("taken_at")
    val takenAt: String?,
    val hasWatermark: Boolean = false
) : Serializable
