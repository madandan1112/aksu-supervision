package cn.aksu.supervision.data.local.dao

import androidx.room.*
import cn.aksu.supervision.data.local.entity.EnterpriseEntity

@Dao
interface EnterpriseDao {

    @Query("SELECT * FROM cached_enterprises ORDER BY name ASC")
    suspend fun getAll(): List<EnterpriseEntity>

    @Query("SELECT * FROM cached_enterprises WHERE name LIKE '%' || :keyword || '%' OR unifiedCode LIKE '%' || :keyword || '%'")
    suspend fun search(keyword: String): List<EnterpriseEntity>

    @Query("SELECT * FROM cached_enterprises WHERE id = :id")
    suspend fun getById(id: Long): EnterpriseEntity?

    @Query("SELECT * FROM cached_enterprises WHERE unifiedCode = :code")
    suspend fun getByCode(code: String): EnterpriseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(enterprises: List<EnterpriseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(enterprise: EnterpriseEntity)

    @Query("DELETE FROM cached_enterprises")
    suspend fun clearAll()
}
