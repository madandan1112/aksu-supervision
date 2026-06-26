package cn.aksu.supervision.data.model

import java.io.Serializable

data class Message(
    val id: Long,
    val title: String,
    val content: String,
    val type: String,           // SYSTEM / TASK / ALERT / CHAT
    val isRead: Boolean,
    val senderId: Long?,
    val senderName: String?,
    val relatedId: Long?,       // 关联业务ID
    val relatedType: String?,   // 关联业务类型
    val createdAt: String
) : Serializable {

    fun typeLabel(): String = when (type) {
        "SYSTEM" -> "系统通知"
        "TASK" -> "任务通知"
        "ALERT" -> "预警通知"
        "CHAT" -> "工作消息"
        else -> type
    }
}
