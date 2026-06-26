<template>
  <div class="page-container">
    <el-page-header @back="goBack" :content="'企业详细信息 - ' + (enterpriseDetail?.enterprise?.name || '')" />
    
    <template v-if="enterpriseDetail">
      <el-card shadow="never" style="margin-top: 16px; margin-bottom: 16px;">
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

      <el-card shadow="never">
        <template #header><span>联系人信息</span></template>
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

    <el-empty v-else description="加载中..." />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getEnterpriseDetailById } from '@/api/system'

const route = useRoute()
const router = useRouter()
const enterpriseDetail = ref(null)

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }
const goBack = () => router.push('/backend/enterprise')

onMounted(async () => {
  const id = route.params.id
  if (!id) { goBack(); return }
  try {
    const res = await getEnterpriseDetailById(id)
    enterpriseDetail.value = res.data
  } catch {
    ElMessage.error('加载企业信息失败')
    goBack()
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
