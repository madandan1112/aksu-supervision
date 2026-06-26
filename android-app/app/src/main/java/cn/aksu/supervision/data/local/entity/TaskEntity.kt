package cn.aksu.supervision.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_tasks")
data class TaskEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val description: String?,
    val type: String,
    val status: String,
    val priority: String,
    val enterpriseId: Long,
    val enterpriseName: String,
    val assigneeId: Long?,
    val assigneeName: String?,
    val deadline: String?,
    val createdAt: String,
    val updatedAt: String?,
    val location: String?,
    /** 离线缓存时间戳 */
    val cachedAt: Long = System.currentTimeMillis()
)
