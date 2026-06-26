package cn.aksu.supervision.data.local.dao

import androidx.room.*
import cn.aksu.supervision.data.local.entity.PendingInspectionEntity

@Dao
interface PendingInspectionDao {

    @Query("SELECT * FROM pending_inspections WHERE isSynced = 0 ORDER BY createdAt DESC")
    suspend fun getUnsynced(): List<PendingInspectionEntity>

    @Query("SELECT * FROM pending_inspections ORDER BY createdAt DESC")
    suspend fun getAll(): List<PendingInspectionEntity>

    @Query("SELECT * FROM pending_inspections WHERE localId = :id")
    suspend fun getById(id: Long): PendingInspectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(inspection: PendingInspectionEntity): Long

    @Update
    suspend fun update(inspection: PendingInspectionEntity)

    @Query("UPDATE pending_inspections SET isSynced = 1 WHERE localId = :id")
    suspend fun markSynced(id: Long)

    @Query("DELETE FROM pending_inspections WHERE isSynced = 1")
    suspend fun clearSynced()
}
