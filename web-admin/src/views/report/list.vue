<template>
  <div class="page-container">
    <!-- 即将到期提示 -->
    <el-alert v-if="expiringReports.length > 0" :title="`${expiringReports.length} 份报告即将到期，请及时处理`" type="warning" show-icon :closable="false" style="margin-bottom: 16px" />

    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已审核" value="APPROVED" />
            <el-option label="已退回" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="企业名/报告类型" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column prop="enterpriseName" label="企业名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="reportType" label="报告类型" width="120">
          <template #default="{ row }"><el-tag size="small">{{ row.reportType }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status] || 'info'" size="small">{{ statusLabel[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expiryDate" label="有效期至" width="120">
          <template #default="{ row }">
            <span :class="{ 'text-danger': isExpiring(row.expiryDate) }">{{ row.expiryDate || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reviewer" label="审核人" width="90" />
        <el-table-column prop="createdAt" label="上传时间" width="165">
          <template #default="{ row }">{{ formatTime(row.createdAt || row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" text type="success" size="small" @click="handleReview(row, 'APPROVED')">通过</el-button>
            <el-button v-if="row.status === 'PENDING'" text type="danger" size="small" @click="handleReview(row, 'REJECTED')">退回</el-button>
            <span v-if="row.status !== 'PENDING'" class="text-muted">已处理</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog v-model="reviewVisible" :title="reviewAction === 'APPROVED' ? '审核通过' : '退回报告'" width="450px">
      <p>确定{{ reviewAction === 'APPROVED' ? '通过' : '退回' }}该报告？</p>
      <el-input v-model="reviewComment" type="textarea" :rows="3" placeholder="请输入审核意见" style="margin-top:12px" />
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button :type="reviewAction === 'APPROVED' ? 'success' : 'danger'" @click="submitReview">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReportList, reviewReport, getExpiringReports } from '@/api/report'

const loading = ref(false)
const total = ref(0)
const reviewVisible = ref(false)
const currentRow = ref(null)
const reviewAction = ref('')
const reviewComment = ref('')
const expiringReports = ref([])

const statusMap = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
const statusLabel = { PENDING: '待审核', APPROVED: '已审核', REJECTED: '已退回' }
const queryParams = reactive({ status: '', keyword: '', page: 1, size: 10 })
const tableData = ref([])

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }
const isExpiring = (date) => { if (!date) return false; const d = new Date(date); const now = new Date(); return d - now < 30 * 24 * 3600 * 1000 && d > now }

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.status) params.status = queryParams.status
    if (queryParams.keyword) params.keyword = queryParams.keyword
    const res = await getReportList(params)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const fetchExpiring = async () => {
  try {
    const res = await getExpiringReports()
    expiringReports.value = res.data || []
  } catch { /* ignore */ }
}

const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.status = ''; queryParams.keyword = ''; handleSearch() }

const handleReview = (row, action) => {
  currentRow.value = row
  reviewAction.value = action
  reviewComment.value = ''
  reviewVisible.value = true
}

const submitReview = async () => {
  try {
    await reviewReport(currentRow.value.id, { status: reviewAction.value, comment: reviewComment.value })
    ElMessage.success(reviewAction.value === 'APPROVED' ? '审核通过' : '已退回')
    reviewVisible.value = false
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

onMounted(() => { fetchData(); fetchExpiring() })
</script>

<style lang="scss" scoped>
.text-danger { color: #f5222d; font-weight: 600; }
.text-muted { color: #c0c4cc; font-size: 13px; }
</style>
