package cn.aksu.supervision.data.api

import cn.aksu.supervision.data.model.*
import retrofit2.http.*

interface ApiService {

    // ========== 认证 ==========

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Field("refresh_token") refreshToken: String): ApiResponse<LoginResponse>

    // ========== 用户 ==========

    @GET("api/users/me")
    suspend fun getProfile(): ApiResponse<User>

    @PUT("api/users/me")
    suspend fun updateProfile(@Body user: User): ApiResponse<User>

    @PUT("api/users/me/password")
    suspend fun changePassword(
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String
    ): ApiResponse<Unit>

    // ========== 任务 ==========

    @GET("api/tasks")
    suspend fun getTasks(
        @Query("status") status: String? = null,
        @Query("type") type: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PagedResponse<Task>>

    @GET("api/tasks/{id}")
    suspend fun getTask(@Path("id") id: Long): ApiResponse<Task>

    @POST("api/tasks/{id}/claim")
    suspend fun claimTask(@Path("id") id: Long): ApiResponse<Task>

    @POST("api/tasks/{id}/delay")
    suspend fun delayTask(
        @Path("id") id: Long,
        @Field("reason") reason: String,
        @Field("new_deadline") newDeadline: String
    ): ApiResponse<Task>

    @GET("api/tasks/statistics")
    suspend fun getTaskStatistics(): ApiResponse<TaskStat>

    // ========== 检查 ==========

    @GET("api/inspections")
    suspend fun getInspections(
        @Query("task_id") taskId: Long? = null,
        @Query("enterprise_id") enterpriseId: Long? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PagedResponse<InspectionRecord>>

    @GET("api/inspections/{id}")
    suspend fun getInspection(@Path("id") id: Long): ApiResponse<InspectionRecord>

    @POST("api/inspections")
    suspend fun submitInspection(@Body record: InspectionRecord): ApiResponse<InspectionRecord>

    @PUT("api/inspections/{id}")
    suspend fun updateInspection(
        @Path("id") id: Long,
        @Body record: InspectionRecord
    ): ApiResponse<InspectionRecord>

    @GET("api/inspection-templates")
    suspend fun getInspectionTemplates(
        @Query("category") category: String? = null
    ): ApiResponse<List<InspectionTemplate>>

    @Multipart
    @POST("api/inspections/photos")
    suspend fun uploadPhoto(
        @Part("file") file: okhttp3.MultipartBody.Part
    ): ApiResponse<Map<String, String>>

    // ========== 企业 ==========

    @GET("api/enterprises")
    suspend fun getEnterprises(
        @Query("keyword") keyword: String? = null,
        @Query("industry") industry: String? = null,
        @Query("risk_level") riskLevel: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PagedResponse<Enterprise>>

    @GET("api/enterprises/{id}")
    suspend fun getEnterprise(@Path("id") id: Long): ApiResponse<Enterprise>

    @GET("api/enterprises/search")
    suspend fun searchEnterprise(@Query("q") query: String): ApiResponse<List<Enterprise>>

    @GET("api/enterprises/by-code/{code}")
    suspend fun getEnterpriseByCode(@Path("code") code: String): ApiResponse<Enterprise>

    // ========== 整改 ==========

    @GET("api/rectifications")
    suspend fun getRectifications(
        @Query("status") status: String? = null,
        @Query("enterprise_id") enterpriseId: Long? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PagedResponse<RectificationNotice>>

    @GET("api/rectifications/{id}")
    suspend fun getRectification(@Path("id") id: Long): ApiResponse<RectificationNotice>

    @POST("api/rectifications/{id}/accept")
    suspend fun acceptRectification(
        @Path("id") id: Long,
        @Field("conclusion") conclusion: String,
        @Field("remark") remark: String?
    ): ApiResponse<RectificationNotice>

    // ========== 预警 ==========

    @GET("api/alerts")
    suspend fun getAlerts(
        @Query("level") level: String? = null,
        @Query("is_read") isRead: Boolean? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PagedResponse<Alert>>

    @GET("api/alerts/{id}")
    suspend fun getAlert(@Path("id") id: Long): ApiResponse<Alert>

    @POST("api/alerts/{id}/handle")
    suspend fun handleAlert(
        @Path("id") id: Long,
        @Field("result") result: String
    ): ApiResponse<Alert>

    @PUT("api/alerts/{id}/read")
    suspend fun markAlertRead(@Path("id") id: Long): ApiResponse<Unit>

    // ========== 消息 ==========

    @GET("api/messages")
    suspend fun getMessages(
        @Query("type") type: String? = null,
        @Query("is_read") isRead: Boolean? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PagedResponse<Message>>

    @PUT("api/messages/{id}/read")
    suspend fun markMessageRead(@Path("id") id: Long): ApiResponse<Unit>

    @PUT("api/messages/read-all")
    suspend fun markAllMessagesRead(): ApiResponse<Unit>

    @GET("api/messages/unread-count")
    suspend fun getUnreadCount(): ApiResponse<Int>
}
