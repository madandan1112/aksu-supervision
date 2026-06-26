<template>
  <div class="page-container">
    <el-card class="filter-card" shadow="never">
      <el-form :model="scanForm" inline>
        <el-form-item label="扫码查询">
          <el-input v-model="scanForm.code" placeholder="输入企业ID或信用代码" clearable style="width: 240px">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleScan">查询企业</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 企业详细信息展示 -->
    <template v-if="enterpriseDetail">
      <el-card shadow="never" style="margin-bottom: 16px;">
        <template #header>
          <div class="section-header">
            <span>企业基本信息</span>
            <el-tag :type="enterpriseDetail.enterprise.status === 1 ? 'success' : 'danger'" size="small">
              {{ enterpriseDetail.enterprise.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </div>
        </template>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="企业名称">{{ enterpriseDetail.enterprise.name }}</el-descriptions-item>
          <el-descriptions-item label="统一信用代码">{{ enterpriseDetail.enterprise.creditCode }}</el-descriptions-item>
          <el-descriptions-item label="法人代表">{{ enterpriseDetail.enterprise.legalPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ enterpriseDetail.enterprise.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ enterpriseDetail.enterprise.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属行业">{{ enterpriseDetail.enterprise.industry || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属区域">{{ enterpriseDetail.enterprise.area || '-' }}</el-descriptions-item>
          <el-descriptions-item label="详细地址" :span="2">{{ enterpriseDetail.enterprise.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="经营范围" :span="3">{{ enterpriseDetail.enterprise.businessScope || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(enterpriseDetail.enterprise.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatTime(enterpriseDetail.enterprise.updateTime) }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 联系人信息 -->
      <el-card shadow="never">
        <template #header>
          <span>联系人信息</span>
        </template>
        <el-table :data="enterpriseDetail.contacts" stripe>
          <el-table-column prop="contactName" label="姓名" width="120" />
          <el-table-column prop="contactPhone" label="电话" width="140" />
          <el-table-column prop="position" label="职务" width="120">
            <template #default="{ row }">{{ row.position || '-' }}</template>
          </el-table-column>
          <el-table-column prop="email" label="邮箱" width="180">
            <template #default="{ row }">{{ row.email || '-' }}</template>
          </el-table-column>
          <el-table-column prop="isPrimary" label="主要联系人" width="100">
            <template #default="{ row }">
              <el-tag :type="row.isPrimary === 1 ? 'warning' : 'info'" size="small">
                {{ row.isPrimary === 1 ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </template>

    <!-- 无数据提示 -->
    <el-empty v-else-if="!loading" description="请输入企业ID或信用代码查询企业详细信息" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getEnterpriseDetailByCode, getEnterpriseDetailById } from '@/api/system'

const route = useRoute()
const loading = ref(false)
const enterpriseDetail = ref(null)
const scanForm = reactive({ code: '' })

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const handleScan = async () => {
  if (!scanForm.code.trim()) {
    ElMessage.warning('请输入企业ID或信用代码')
    return
  }
  loading.value = true
  try {
    const res = await getEnterpriseDetailByCode(scanForm.code.trim())
    enterpriseDetail.value = res.data
    ElMessage.success('查询成功')
  } catch (e) {
    enterpriseDetail.value = null
    ElMessage.error(e.response?.data?.message || '查询失败，企业不存在')
  } finally { loading.value = false }
}

// 如果URL有userId参数，自动查询该用户关联的企业
onMounted(async () => {
  const userId = route.query.userId
  if (userId) {
    loading.value = true
    try {
      // 通过企业列表API找到该用户关联的企业
      const { getEnterpriseList } = await import('@/api/enterprise')
      const res = await getEnterpriseList({ page: 1, size: 100 })
      const enterprises = res.data?.list || []
      const ent = enterprises.find(e => e.userId === Number(userId))
      if (ent) {
        const detailRes = await getEnterpriseDetailById(ent.id)
        enterpriseDetail.value = detailRes.data
        scanForm.code = ent.creditCode || String(ent.id)
      } else {
        ElMessage.info('该用户暂未关联企业')
      }
    } catch { /* ignore */ } finally { loading.value = false }
  }
})
</script>

<style lang="scss" scoped>
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
