package cn.aksu.supervision.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_inspections")
data class PendingInspectionEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val taskId: Long,
    val enterpriseId: Long,
    val enterpriseName: String,
    val inspectorId: Long,
    val inspectorName: String,
    val inspectionDate: String,
    val itemsJson: String,          // JSON 序列化的检查项
    val photosJson: String?,        // JSON 序列化的照片列表
    val conclusion: String,
    val remark: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    /** 是否已同步到服务器 */
    val isSynced: Boolean = false,
    /** 创建时间 */
    val createdAt: Long = System.currentTimeMillis()
)
