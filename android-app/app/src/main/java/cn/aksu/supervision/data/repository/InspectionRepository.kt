package cn.aksu.supervision.data.repository

import cn.aksu.supervision.data.api.ApiService
import cn.aksu.supervision.data.local.dao.PendingInspectionDao
import cn.aksu.supervision.data.local.entity.PendingInspectionEntity
import cn.aksu.supervision.data.model.InspectionRecord
import cn.aksu.supervision.data.model.InspectionTemplate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InspectionRepository @Inject constructor(
    private val apiService: ApiService,
    private val pendingInspectionDao: PendingInspectionDao,
    private val gson: Gson
) {

    /** 提交检查记录（在线优先，离线存本地） */
    suspend fun submitInspection(record: InspectionRecord): Result<InspectionRecord> {
        return try {
            val response = apiService.submitInspection(record)
            if (response.isSuccessful && response.data != null) {
                Result.success(response.data)
            } else {
                // 接口返回异常，存入离线队列
                saveOffline(record)
                Result.success(record.copy(status = "DRAFT"))
            }
        } catch (e: Exception) {
            // 网络异常，存入离线队列
            saveOffline(record)
            Result.success(record.copy(status = "DRAFT"))
        }
    }

    /** 获取检查模板 */
    suspend fun getTemplates(category: String? = null): Result<List<InspectionTemplate>> {
        return try {
            val response = apiService.getInspectionTemplates(category)
            if (response.isSuccessful && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 获取检查记录 */
    fun getInspections(taskId: Long? = null, page: Int = 0, size: Int = 20): Flow<Result<List<InspectionRecord>>> = flow {
        try {
            val response = apiService.getInspections(taskId = taskId, page = page, size = size)
            if (response.isSuccessful && response.data != null) {
                emit(Result.success(response.data.content))
            } else {
                emit(Result.failure(Exception(response.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /** 保存离线检查数据 */
    private suspend fun saveOffline(record: InspectionRecord) {
        val entity = PendingInspectionEntity(
            taskId = record.taskId,
            enterpriseId = record.enterpriseId,
            enterpriseName = record.enterpriseName,
            inspectorId = record.inspectorId,
            inspectorName = record.inspectorName,
            inspectionDate = record.inspectionDate,
            itemsJson = gson.toJson(record.items),
            photosJson = record.photos?.let { gson.toJson(it) },
            conclusion = record.conclusion,
            remark = record.remark,
            latitude = record.latitude,
            longitude = record.longitude,
            address = record.address,
            isSynced = false
        )
        pendingInspectionDao.insert(entity)
    }

    /** 同步离线数据到服务器 */
    suspend fun syncOfflineData(): Result<Int> {
        val unsynced = pendingInspectionDao.getUnsynced()
        var syncedCount = 0
        for (entity in unsynced) {
            try {
                val itemsType = object : TypeToken<List<cn.aksu.supervision.data.model.InspectionItem>>() {}.type
                val items: List<cn.aksu.supervision.data.model.InspectionItem> = gson.fromJson(entity.itemsJson, itemsType)
                val photosType = object : TypeToken<List<cn.aksu.supervision.data.model.InspectionPhoto>>() {}.type
                val photos: List<cn.aksu.supervision.data.model.InspectionPhoto>? = entity.photosJson?.let {
                    gson.fromJson(it, photosType)
                }

                val record = InspectionRecord(
                    id = 0,
                    taskId = entity.taskId,
                    enterpriseId = entity.enterpriseId,
                    enterpriseName = entity.enterpriseName,
                    inspectorId = entity.inspectorId,
                    inspectorName = entity.inspectorName,
                    inspectionDate = entity.inspectionDate,
                    items = items,
                    photos = photos,
                    conclusion = entity.conclusion,
                    remark = entity.remark,
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    address = entity.address,
                    status = "SUBMITTED",
                    createdAt = "",
                    updatedAt = null
                )

                val response = apiService.submitInspection(record)
                if (response.isSuccessful) {
                    pendingInspectionDao.markSynced(entity.localId)
                    syncedCount++
                }
            } catch (_: Exception) {
                // 单条失败继续同步下一条
            }
        }
        // 清理已同步记录
        pendingInspectionDao.clearSynced()
        return Result.success(syncedCount)
    }
}
