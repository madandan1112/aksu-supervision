package cn.aksu.supervision.data.local.dao

import androidx.room.*
import cn.aksu.supervision.data.local.entity.AlertEntity

@Dao
interface AlertDao {

    @Query("SELECT * FROM cached_alerts ORDER BY createdAt DESC")
    suspend fun getAll(): List<AlertEntity>

    @Query("SELECT * FROM cached_alerts WHERE isRead = 0 ORDER BY createdAt DESC")
    suspend fun getUnread(): List<AlertEntity>

    @Query("SELECT * FROM cached_alerts WHERE id = :id")
    suspend fun getById(id: Long): AlertEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alerts: List<AlertEntity>)

    @Query("UPDATE cached_alerts SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: Long)

    @Query("DELETE FROM cached_alerts")
    suspend fun clearAll()
}
