package cn.aksu.supervision.data.repository

import cn.aksu.supervision.data.api.ApiService
import cn.aksu.supervision.data.local.dao.EnterpriseDao
import cn.aksu.supervision.data.local.entity.EnterpriseEntity
import cn.aksu.supervision.data.model.Enterprise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnterpriseRepository @Inject constructor(
    private val apiService: ApiService,
    private val enterpriseDao: EnterpriseDao
) {

    /** 搜索企业 */
    fun search(keyword: String? = null, page: Int = 0, size: Int = 20): Flow<Result<List<Enterprise>>> = flow {
        try {
            val response = apiService.getEnterprises(keyword = keyword, page = page, size = size)
            if (response.isSuccessful && response.data != null) {
                val enterprises = response.data.content
                enterpriseDao.insertAll(enterprises.map { it.toEntity() })
                emit(Result.success(enterprises))
            } else {
                val cached = if (!keyword.isNullOrBlank()) enterpriseDao.search(keyword) else enterpriseDao.getAll()
                emit(Result.success(cached.map { it.toModel() }))
            }
        } catch (e: Exception) {
            val cached = if (!keyword.isNullOrBlank()) enterpriseDao.search(keyword) else enterpriseDao.getAll()
            if (cached.isNotEmpty()) {
                emit(Result.success(cached.map { it.toModel() }))
            } else {
                emit(Result.failure(e))
            }
        }
    }

    /** 获取企业详情 */
    suspend fun getEnterprise(id: Long): Result<Enterprise> {
        return try {
            val response = apiService.getEnterprise(id)
            if (response.isSuccessful && response.data != null) {
                enterpriseDao.insert(response.data.toEntity())
                Result.success(response.data)
            } else {
                val cached = enterpriseDao.getById(id)
                if (cached != null) Result.success(cached.toModel())
                else Result.failure(Exception("企业不存在"))
            }
        } catch (e: Exception) {
            val cached = enterpriseDao.getById(id)
            if (cached != null) Result.success(cached.toModel())
            else Result.failure(e)
        }
    }

    /** 扫码查询企业（通过统一社会信用代码） */
    suspend fun getEnterpriseByCode(code: String): Result<Enterprise> {
        return try {
            val response = apiService.getEnterpriseByCode(code)
            if (response.isSuccessful && response.data != null) {
                enterpriseDao.insert(response.data.toEntity())
                Result.success(response.data)
            } else {
                val cached = enterpriseDao.getByCode(code)
                if (cached != null) Result.success(cached.toModel())
                else Result.failure(Exception("企业不存在"))
            }
        } catch (e: Exception) {
            val cached = enterpriseDao.getByCode(code)
            if (cached != null) Result.success(cached.toModel())
            else Result.failure(e)
        }
    }
}

private fun Enterprise.toEntity() = EnterpriseEntity(
    id = id, name = name, unifiedCode = unifiedCode,
    legalPerson = legalPerson, phone = phone, address = address,
    longitude = longitude, latitude = latitude, industry = industry,
    category = category, riskLevel = riskLevel, supervisionType = supervisionType,
    status = status, lastInspectionDate = lastInspectionDate,
    nextInspectionDate = nextInspectionDate, inspectionCount = inspectionCount,
    qrCode = qrCode
)

private fun EnterpriseEntity.toModel() = Enterprise(
    id = id, name = name, unifiedCode = unifiedCode,
    legalPerson = legalPerson, phone = phone, address = address,
    longitude = longitude, latitude = latitude, industry = industry,
    category = category, riskLevel = riskLevel, supervisionType = supervisionType,
    status = status, lastInspectionDate = lastInspectionDate,
    nextInspectionDate = nextInspectionDate, inspectionCount = inspectionCount,
    rectificationCount = 0, qrCode = qrCode, createdAt = "", updatedAt = null
)
