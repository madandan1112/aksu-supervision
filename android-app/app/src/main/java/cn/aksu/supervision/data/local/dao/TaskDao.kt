package cn.aksu.supervision.data.local.dao

import androidx.room.*
import cn.aksu.supervision.data.local.entity.TaskEntity

@Dao
interface TaskDao {

    @Query("SELECT * FROM cached_tasks ORDER BY createdAt DESC")
    suspend fun getAll(): List<TaskEntity>

    @Query("SELECT * FROM cached_tasks WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getByStatus(status: String): List<TaskEntity>

    @Query("SELECT * FROM cached_tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Query("DELETE FROM cached_tasks")
    suspend fun clearAll()

    @Query("DELETE FROM cached_tasks WHERE cachedAt < :timestamp")
    suspend fun clearOlderThan(timestamp: Long)
}
