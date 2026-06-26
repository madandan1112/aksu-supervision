package cn.aksu.supervision.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_alerts")
data class AlertEntity(
    @PrimaryKey
    val id: Long,
    val type: String,
    val level: String,
    val title: String,
    val content: String,
    val enterpriseId: Long?,
    val enterpriseName: String?,
    val taskId: Long?,
    val isRead: Boolean,
    val createdAt: String,
    val cachedAt: Long = System.currentTimeMillis()
)
