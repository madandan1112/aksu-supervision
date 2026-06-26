package cn.aksu.supervision.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Alert(
    val id: Long,
    val type: String,               // OVERDUE / RISK / COMPLAINT / SYSTEM
    val level: String,              // INFO / WARNING / CRITICAL
    val title: String,
    val content: String,
    val enterpriseId: Long?,
    val enterpriseName: String?,
    val taskId: Long?,
    val isRead: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    val handledAt: String?,
    val handlerId: Long?,
    val handlerName: String?,
    val handleResult: String?
) : Serializable {

    fun typeLabel(): String = when (type) {
        "OVERDUE" -> "逾期预警"
        "RISK" -> "风险预警"
        "COMPLAINT" -> "投诉预警"
        "SYSTEM" -> "系统通知"
        else -> type
    }

    fun levelLabel(): String = when (level) {
        "INFO" -> "提示"
        "WARNING" -> "警告"
        "CRITICAL" -> "严重"
        else -> level
    }
}
