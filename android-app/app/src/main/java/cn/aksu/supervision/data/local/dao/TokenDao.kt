package cn.aksu.supervision.data.local.dao

import androidx.room.*

@Dao
interface TokenDao {

    @Query("SELECT accessToken FROM tokens WHERE id = 0")
    suspend fun getAccessToken(): String?

    @Query("SELECT refreshToken FROM tokens WHERE id = 0")
    suspend fun getRefreshToken(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveToken(entity: cn.aksu.supervision.data.local.entity.TokenEntity)

    @Query("DELETE FROM tokens")
    suspend fun clearToken()
}
