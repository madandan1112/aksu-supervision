package cn.aksu.supervision.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tokens")
data class TokenEntity(
    @PrimaryKey
    val id: Int = 0,
    val accessToken: String,
    val refreshToken: String?,
    val expireAt: Long?
)
