<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">现场检查管理</h2>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>执法管理</el-breadcrumb-item>
          <el-breadcrumb-item>现场检查</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <el-card shadow="never" class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #409eff, #79bbff)">
          <el-icon size="24"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total || 0 }}</div>
          <div class="stat-label">检查总数</div>
        </div>
      </el-card>
    </div>

    <!-- 筛选栏 -->
    <el-card shadow="never" class="filter-card">
      <el-form inline>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部状态" style="width: 140px">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已提交" value="SUBMITTED" />
            <el-option label="整改中" value="RECTIFYING" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业ID">
          <el-input v-model="queryForm.enterpriseId" clearable placeholder="企业ID" style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表 -->
    <el-card shadow="never">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="enterpriseName" label="企业名称" min-width="160">
          <template #default="{ row }">
            <el-link type="primary" @click="viewEnterprise(row)">{{ row.enterpriseName || '-' }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="inspectorName" label="检查人员" width="100" />
        <el-table-column prop="checkType" label="检查类型" min-width="140" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="inspectionTime" label="检查时间" width="170">
          <template #default="{ row }">{{ formatTime(row.inspectionTime) }}</template>
        </el-table-column>
        <el-table-column prop="summary" label="检查摘要" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="检查记录详情" width="700px" destroy-on-close>
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ detail.enterpriseName }}</el-descriptions-item>
        <el-descriptions-item label="检查人员">{{ detail.inspectorName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detail.status)" size="small">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="检查时间" :span="2">{{ formatTime(detail.inspectionTime) }}</el-descriptions-item>
        <el-descriptions-item label="检查类型" :span="2">{{ detail.checkType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="2">
          <div style="white-space: pre-wrap">{{ detail.issues || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="检查摘要" :span="2">
          <div style="white-space: pre-wrap">{{ detail.summary || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="附件" :span="2">
          <div v-if="detail.attachmentUrls">
            <el-link v-for="(url, i) in parseUrls(detail.attachmentUrls)" :key="i" type="primary" :href="url" target="_blank">
              附件{{ i + 1 }}
            </el-link>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="证据图片" :span="2">
          <div v-if="detail.evidenceImages" class="evidence-images">
            <el-image v-for="(url, i) in parseUrls(detail.evidenceImages)" :key="i" :src="url" :preview-src-list="parseUrls(detail.evidenceImages)" fit="cover" style="width:80px;height:80px;margin-right:8px" />
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 企业详情弹窗 -->
    <el-dialog v-model="enterpriseVisible" title="企业详情" width="600px" destroy-on-close>
      <el-descriptions :column="2" border v-if="enterpriseDetail">
        <el-descriptions-item label="企业名称">{{ enterpriseDetail.name }}</el-descriptions-item>
        <el-descriptions-item label="信用代码">{{ enterpriseDetail.creditCode }}</el-descriptions-item>
        <el-descriptions-item label="法人">{{ enterpriseDetail.legalPerson }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ enterpriseDetail.phone }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ enterpriseDetail.industry }}</el-descriptions-item>
        <el-descriptions-item label="地区">{{ enterpriseDetail.region }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ enterpriseDetail.address }}</el-descriptions-item>
        <el-descriptions-item label="经营范围" :span="2">{{ enterpriseDetail.businessScope }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { getAdminInspectionList, getAdminInspectionDetail, getAdminInspectionStats } from '@/api/inspection'
import { getEnterpriseDetail } from '@/api/enterprise'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const stats = ref({})
const queryForm = ref({ page: 1, size: 10, status: '', enterpriseId: '' })

const detailVisible = ref(false)
const detail = ref(null)
const enterpriseVisible = ref(false)
const enterpriseDetail = ref(null)

const statusLabel = (s) => ({ DRAFT: '草稿', SUBMITTED: '已提交', RECTIFYING: '整改中', COMPLETED: '已完成' }[s] || s)
const statusType = (s) => ({ DRAFT: 'info', SUBMITTED: '', RECTIFYING: 'warning', COMPLETED: 'success' }[s] || 'info')
const formatTime = (t) => t ? t.replace('T', ' ').substring(0, 19) : '-'
const parseUrls = (str) => str ? str.split(',').filter(s => s.trim()) : []

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: queryForm.value.page, size: queryForm.value.size }
    if (queryForm.value.status) params.status = queryForm.value.status
    if (queryForm.value.enterpriseId) params.enterpriseId = queryForm.value.enterpriseId
    const res = await getAdminInspectionList(params)
    tableData.value = res.data?.list || res.data?.content || res.data?.items || []
    total.value = res.data?.total || res.data?.totalElements || 0
  } catch (e) { ElMessage.error('加载数据失败') }
  finally { loading.value = false }
}

const loadStats = async () => {
  try {
    const res = await getAdminInspectionStats()
    stats.value = res.data || {}
  } catch (e) { /* ignore */ }
}

const resetQuery = () => {
  queryForm.value = { page: 1, size: 10, status: '', enterpriseId: '' }
  loadData()
}

const viewDetail = async (row) => {
  try {
    const res = await getAdminInspectionDetail(row.id)
    detail.value = res.data
    detailVisible.value = true
  } catch (e) { ElMessage.error('加载详情失败') }
}

const viewEnterprise = async (row) => {
  if (!row.enterpriseId) return ElMessage.warning('无企业信息')
  try {
    const res = await getEnterpriseDetail(row.enterpriseId)
    enterpriseDetail.value = res.data
    enterpriseVisible.value = true
  } catch (e) { ElMessage.error('加载企业详情失败') }
}

onMounted(() => { loadData(); loadStats() })
</script>

<style lang="scss" scoped>
.page-container { background: #f5f7fa; min-height: 100vh; padding: 20px; }
.page-header { margin-bottom: 16px; .header-left { .page-title { margin: 0 0 8px 0; font-size: 20px; font-weight: 600; } } }
.stat-cards { display: flex; gap: 16px; margin-bottom: 16px; }
.stat-card { flex: 1; cursor: pointer; :deep(.el-card__body) { display: flex; align-items: center; gap: 16px; padding: 20px; }
  .stat-icon { width: 52px; height: 52px; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #fff; }
  .stat-info { .stat-value { font-size: 28px; font-weight: 700; color: #303133; } .stat-label { font-size: 13px; color: #909399; margin-top: 2px; } }
}
.filter-card { margin-bottom: 16px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
.evidence-images { display: flex; flex-wrap: wrap; }
</style>
