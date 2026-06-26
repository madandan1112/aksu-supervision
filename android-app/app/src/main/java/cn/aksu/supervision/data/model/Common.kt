package cn.aksu.supervision.data.model

/**
 * 通用API响应包装
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    val isSuccessful: Boolean get() = code == 0
}

/**
 * 分页响应
 */
data class PagedResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val page: Int,
    val size: Int
)

/**
 * 登录请求
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * 登录响应
 */
data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: User
)

/**
 * 检查模板
 */
data class InspectionTemplate(
    val id: Long,
    val name: String,
    val category: String,
    val items: List<InspectionItem>
)

/**
 * 任务统计
 */
data class TaskStat(
    val total: Int,
    val pending: Int,
    val inProgress: Int,
    val completed: Int,
    val delayed: Int,
    val completionRate: Float
)
