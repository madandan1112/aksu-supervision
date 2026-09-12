<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">整改管理</h2>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>执法管理</el-breadcrumb-item>
          <el-breadcrumb-item>整改管理</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <el-card shadow="never" class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #e6a23c, #f0c78a)">
          <el-icon size="24"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total || 0 }}</div>
          <div class="stat-label">整改通知总数</div>
        </div>
      </el-card>
      <el-card shadow="never" class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f56c6c, #f89898)">
          <el-icon size="24"><Bell /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.issued || 0 }}</div>
          <div class="stat-label">整改中</div>
        </div>
      </el-card>
      <el-card shadow="never" class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #67c23a, #95d475)">
          <el-icon size="24"><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.completed || 0 }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </el-card>
    </div>

    <!-- 筛选栏 -->
    <el-card shadow="never" class="filter-card">
      <el-form inline>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部状态" style="width: 140px">
            <el-option label="已下发" value="ISSUED" />
            <el-option label="整改中" value="RECTIFYING" />
            <el-option label="待验收" value="PENDING_ACCEPTANCE" />
            <el-option label="已完成" value="ACCEPTED" />
            <el-option label="已逾期" value="OVERDUE" />
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
        <el-table-column prop="noticeNo" label="通知书编号" width="160" />
        <el-table-column prop="enterpriseId" label="企业ID" width="80" />
        <el-table-column prop="inspectorId" label="执法人员ID" width="100" />
        <el-table-column prop="issues" label="问题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="requirements" label="整改要求" min-width="200" show-overflow-tooltip />
        <el-table-column prop="deadline" label="整改期限" width="120">
          <template #default="{ row }">{{ row.deadline ? row.deadline.toString().substring(0, 10) : '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewDetail(row)">查看详情</el-button>
            <el-button type="success" link size="small" @click="viewFeedbacks(row)">整改反馈</el-button>
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
    <el-dialog v-model="detailVisible" title="整改通知书详情" width="700px" destroy-on-close>
      <template v-if="detailData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="通知书编号">{{ detailData.noticeNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(detailData.status)" size="small">{{ statusLabel(detailData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="整改期限">{{ detailData.deadline ? detailData.deadline.toString().substring(0, 10) : '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(detailData.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="问题描述" :span="2">
            <div style="white-space: pre-wrap">{{ detailData.issues }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="整改要求" :span="2">
            <div style="white-space: pre-wrap">{{ detailData.requirements }}</div>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 反馈记录 -->
        <div v-if="detailFeedbacks.length" style="margin-top: 20px">
          <h4 style="margin: 0 0 12px 0; font-size: 15px; color: #303133;">整改反馈记录</h4>
          <el-timeline>
            <el-timeline-item v-for="fb in detailFeedbacks" :key="fb.id" :timestamp="formatTime(fb.createTime)" placement="top">
              <el-card shadow="never">
                <div style="white-space: pre-wrap">{{ fb.content || fb.description }}</div>
                <div v-if="fb.checkType" style="margin-top: 8px; font-size: 12px; color: #909399;">检查类型：{{ fb.checkType }}</div>
                <div v-if="fb.attachmentUrls" style="margin-top: 8px;">
                  <el-link v-for="(url, i) in parseUrls(fb.attachmentUrls)" :key="i" type="primary" :href="url" target="_blank" style="margin-right: 8px;">附件{{ i + 1 }}</el-link>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </template>
    </el-dialog>

    <!-- 反馈列表弹窗 -->
    <el-dialog v-model="feedbackVisible" title="整改反馈记录" width="800px" destroy-on-close>
      <el-timeline v-if="feedbacks.length">
        <el-timeline-item v-for="fb in feedbacks" :key="fb.id" :timestamp="formatTime(fb.createTime)" placement="top">
          <el-card shadow="never">
            <div style="white-space: pre-wrap">{{ fb.content || fb.description }}</div>
            <div v-if="fb.checkType" style="margin-top: 8px; font-size: 12px; color: #909399;">检查类型：{{ fb.checkType }}</div>
            <div v-if="fb.attachmentUrls" style="margin-top: 8px;">
              <el-link v-for="(url, i) in parseUrls(fb.attachmentUrls)" :key="i" type="primary" :href="url" target="_blank" style="margin-right: 8px;">附件{{ i + 1 }}</el-link>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无反馈记录" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Bell, CircleCheck } from '@element-plus/icons-vue'
import { getAdminRectificationList, getAdminRectificationDetail, getAdminRectificationFeedbacks, getAdminRectificationStats } from '@/api/inspection'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const stats = ref({})
const queryForm = ref({ page: 1, size: 10, status: '', enterpriseId: '' })

const detailVisible = ref(false)
const detailData = ref(null)
const detailFeedbacks = ref([])
const feedbackVisible = ref(false)
const feedbacks = ref([])

const statusLabel = (s) => ({
  ISSUED: '已下发', RECTIFYING: '整改中', PENDING_ACCEPTANCE: '待验收',
  ACCEPTED: '已完成', COMPLETED: '已完成', OVERDUE: '已逾期'
}[s] || s)
const statusType = (s) => ({
  ISSUED: 'warning', RECTIFYING: '', PENDING_ACCEPTANCE: 'danger',
  ACCEPTED: 'success', COMPLETED: 'success', OVERDUE: 'danger'
}[s] || 'info')
const formatTime = (t) => t ? t.replace('T', ' ').substring(0, 19) : '-'
const parseUrls = (str) => str ? str.split(',').filter(s => s.trim()) : []

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: queryForm.value.page, size: queryForm.value.size }
    if (queryForm.value.status) params.status = queryForm.value.status
    if (queryForm.value.enterpriseId) params.enterpriseId = queryForm.value.enterpriseId
    const res = await getAdminRectificationList(params)
    tableData.value = res.data?.list || res.data?.content || res.data?.items || []
    total.value = res.data?.total || res.data?.totalElements || res.data?.totalSize || 0
  } catch (e) { ElMessage.error('加载数据失败') }
  finally { loading.value = false }
}

const loadStats = async () => {
  try {
    const res = await getAdminRectificationStats()
    stats.value = res.data || {}
  } catch (e) { /* ignore */ }
}

const resetQuery = () => {
  queryForm.value = { page: 1, size: 10, status: '', enterpriseId: '' }
  loadData()
}

const viewDetail = async (row) => {
  try {
    const res = await getAdminRectificationDetail(row.id)
    detailData.value = res.data?.notice || res.data
    // 同时加载反馈
    try {
      const fbRes = await getAdminRectificationFeedbacks(row.id)
      detailFeedbacks.value = fbRes.data || []
    } catch (e) { detailFeedbacks.value = [] }
    detailVisible.value = true
  } catch (e) { ElMessage.error('加载详情失败') }
}

const viewFeedbacks = async (row) => {
  try {
    const res = await getAdminRectificationFeedbacks(row.id)
    feedbacks.value = res.data || []
    feedbackVisible.value = true
  } catch (e) { ElMessage.error('加载反馈失败') }
}

onMounted(() => { loadData(); loadStats() })
</script>

<style lang="scss" scoped>
.page-container { background: #f5f7fa; min-height: 100vh; padding: 20px; }
.page-header { margin-bottom: 16px; .header-left { .page-title { margin: 0 0 8px 0; font-size: 20px; font-weight: 600; } } }
.stat-cards { display: flex; gap: 16px; margin-bottom: 16px; }
.stat-card { flex: 1; :deep(.el-card__body) { display: flex; align-items: center; gap: 16px; padding: 20px; }
  .stat-icon { width: 52px; height: 52px; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #fff; }
  .stat-info { .stat-value { font-size: 28px; font-weight: 700; color: #303133; } .stat-label { font-size: 13px; color: #909399; margin-top: 2px; } }
}
.filter-card { margin-bottom: 16px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
