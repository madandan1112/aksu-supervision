package cn.aksu.supervision.data.repository

import cn.aksu.supervision.data.api.ApiService
import cn.aksu.supervision.data.local.dao.TaskDao
import cn.aksu.supervision.data.local.entity.TaskEntity
import cn.aksu.supervision.data.model.Task
import cn.aksu.supervision.data.model.TaskStat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val apiService: ApiService,
    private val taskDao: TaskDao
) {

    /** 获取任务列表（在线优先，离线兜底） */
    fun getTasks(status: String? = null, page: Int = 0, size: Int = 20): Flow<Result<List<Task>>> = flow {
        try {
            val response = apiService.getTasks(status = status, page = page, size = size)
            if (response.isSuccessful && response.data != null) {
                val tasks = response.data.content
                // 缓存到本地
                taskDao.insertAll(tasks.map { it.toEntity() })
                emit(Result.success(tasks))
            } else {
                // 接口异常，尝试读取本地缓存
                val cached = if (status != null) taskDao.getByStatus(status) else taskDao.getAll()
                emit(Result.success(cached.map { it.toModel() }))
            }
        } catch (e: Exception) {
            // 网络异常，读取本地缓存
            val cached = if (status != null) taskDao.getByStatus(status) else taskDao.getAll()
            if (cached.isNotEmpty()) {
                emit(Result.success(cached.map { it.toModel() }))
            } else {
                emit(Result.failure(e))
            }
        }
    }

    /** 获取任务详情 */
    suspend fun getTask(id: Long): Result<Task> {
        return try {
            val response = apiService.getTask(id)
            if (response.isSuccessful && response.data != null) {
                taskDao.insert(response.data.toEntity())
                Result.success(response.data)
            } else {
                val cached = taskDao.getById(id)
                if (cached != null) Result.success(cached.toModel())
                else Result.failure(Exception("任务不存在"))
            }
        } catch (e: Exception) {
            val cached = taskDao.getById(id)
            if (cached != null) Result.success(cached.toModel())
            else Result.failure(e)
        }
    }

    /** 认领任务 */
    suspend fun claimTask(id: Long): Result<Task> {
        return try {
            val response = apiService.claimTask(id)
            if (response.isSuccessful && response.data != null) {
                taskDao.insert(response.data.toEntity())
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 延期任务 */
    suspend fun delayTask(id: Long, reason: String, newDeadline: String): Result<Task> {
        return try {
            val response = apiService.delayTask(id, reason, newDeadline)
            if (response.isSuccessful && response.data != null) {
                taskDao.insert(response.data.toEntity())
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 获取任务统计 */
    suspend fun getTaskStatistics(): Result<TaskStat> {
        return try {
            val response = apiService.getTaskStatistics()
            if (response.isSuccessful && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/** Model -> Entity */
private fun Task.toEntity() = TaskEntity(
    id = id, title = title, description = description,
    type = type, status = status, priority = priority,
    enterpriseId = enterpriseId, enterpriseName = enterpriseName,
    assigneeId = assigneeId, assigneeName = assigneeName,
    deadline = deadline, createdAt = createdAt, updatedAt = updatedAt,
    location = location
)

/** Entity -> Model */
private fun TaskEntity.toModel() = Task(
    id = id, title = title, description = description,
    type = type, status = status, priority = priority,
    enterpriseId = enterpriseId, enterpriseName = enterpriseName,
    assigneeId = assigneeId, assigneeName = assigneeName,
    deadline = deadline, createdAt = createdAt, updatedAt = updatedAt,
    location = location, checkItems = null
)
