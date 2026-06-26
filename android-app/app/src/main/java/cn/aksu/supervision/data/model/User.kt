package cn.aksu.supervision.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Long,
    val username: String,
    val realName: String,
    val phone: String?,
    val department: String?,
    val role: String,
    val avatar: String?,
    val token: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?
) {
    /** 是否为监管员 */
    val isSupervisor: Boolean get() = role == "SUPERVISOR"

    /** 是否为管理员 */
    val isAdmin: Boolean get() = role == "ADMIN"

    /** 显示名：优先真实姓名 */
    val displayName: String get() = realName.ifBlank { username }
}
