package cn.aksu.supervision.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_enterprises")
data class EnterpriseEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val unifiedCode: String,
    val legalPerson: String?,
    val phone: String?,
    val address: String?,
    val longitude: Double?,
    val latitude: Double?,
    val industry: String?,
    val category: String?,
    val riskLevel: String?,
    val supervisionType: String?,
    val status: String?,
    val lastInspectionDate: String?,
    val nextInspectionDate: String?,
    val inspectionCount: Int = 0,
    val qrCode: String?,
    val cachedAt: Long = System.currentTimeMillis()
)
