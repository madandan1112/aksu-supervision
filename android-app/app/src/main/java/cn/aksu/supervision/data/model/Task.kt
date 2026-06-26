package cn.aksu.supervision.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Task(
    val id: Long,
    val title: String,
    val description: String?,
    val type: String,               // ROUTINE / SPECIAL / COMPLAINT
    val status: String,             // PENDING / CLAIMED / IN_PROGRESS / COMPLETED / DELAYED
    val priority: String,           // LOW / MEDIUM / HIGH / URGENT
    val enterpriseId: Long,
    val enterpriseName: String,
    val assigneeId: Long?,
    val assigneeName: String?,
    val deadline: String?,
    val createdAt: String,
    val updatedAt: String?,
    @SerializedName("check_items")
    val checkItems: List<String>?,
    val location: String?
) : Serializable {

    /** 状态的中文显示名 */
    fun statusLabel(): String = when (status) {
        "PENDING" -> "待认领"
        "CLAIMED" -> "已认领"
        "IN_PROGRESS" -> "进行中"
        "COMPLETED" -> "已完成"
        "DELAYED" -> "已延期"
        else -> status
    }

    /** 类型的中文显示名 */
    fun typeLabel(): String = when (type) {
        "ROUTINE" -> "日常检查"
        "SPECIAL" -> "专项检查"
        "COMPLAINT" -> "投诉核查"
        else -> type
    }

    /** 优先级的中文显示名 */
    fun priorityLabel(): String = when (priority) {
        "LOW" -> "低"
        "MEDIUM" -> "中"
        "HIGH" -> "高"
        "URGENT" -> "紧急"
        else -> priority
    }
}
