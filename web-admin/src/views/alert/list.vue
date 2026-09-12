<template>
  <div class="page-container">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <h3>预警管理</h3>
      <div>
        <el-button type="primary" @click="triggerScan" :loading="scanLoading">
          <el-icon><Refresh /></el-icon> 手动扫描
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-row">
      <div class="stat-item" @click="filterByStatus('')">
        <div class="stat-num">{{ stats.total || 0 }}</div>
        <div class="stat-label">预警总数</div>
      </div>
      <div class="stat-item pending" @click="filterByStatus('PENDING')">
        <div class="stat-num">{{ stats.pending || 0 }}</div>
        <div class="stat-label">待处理</div>
      </div>
      <div class="stat-item handling" @click="filterByStatus('HANDLING')">
        <div class="stat-num">{{ stats.handling || 0 }}</div>
        <div class="stat-label">处理中</div>
      </div>
      <div class="stat-item handled" @click="filterByStatus('HANDLED')">
        <div class="stat-num">{{ stats.handled || 0 }}</div>
        <div class="stat-label">已处理</div>
      </div>
      <div class="stat-item pending" @click="filterByStatus('ESCALATED')">
        <div class="stat-num">{{ stats.escalated || 0 }}</div>
        <div class="stat-label">已督办</div>
      </div>
      <div class="stat-item high" @click="filterByLevel('HIGH')">
        <div class="stat-num">{{ stats.highLevel || 0 }}</div>
        <div class="stat-label">高风险</div>
      </div>
    </div>

    <!-- 筛选 -->
    <el-card shadow="never" style="margin-bottom:12px">
      <el-form :model="queryParams" inline>
        <el-form-item label="等级">
          <el-select v-model="queryParams.level" placeholder="全部" clearable style="width: 100px">
            <el-option label="高" value="HIGH" /><el-option label="中" value="MEDIUM" /><el-option label="低" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 100px">
            <el-option label="待处理" value="PENDING" /><el-option label="处理中" value="HANDLING" />
            <el-option label="已处理" value="HANDLED" /><el-option label="已督办" value="ESCALATED" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 160px">
            <el-option v-for="t in alertTypeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表 -->
    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe size="small">
        <el-table-column prop="id" label="ID" width="55" />
        <el-table-column prop="level" label="等级" width="65" align="center">
          <template #default="{ row }">
            <el-tag :type="levelColor[row.level] || 'info'" size="small" effect="dark">{{ levelLabel[row.level] || row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertType" label="预警类型" width="140">
          <template #default="{ row }">
            <span>{{ typeLabelMap[row.alertType] || row.alertType }}</span>
            <el-tag v-if="row.source === 'EVENT'" type="success" size="small" style="margin-left:4px">实时</el-tag>
            <el-tag v-else size="small" style="margin-left:4px">定时</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="预警内容" min-width="220" show-overflow-tooltip />
        <el-table-column prop="enterpriseName" label="关联企业" width="140" show-overflow-tooltip />
        <el-table-column prop="assignedUserName" label="派单人员" width="90">
          <template #default="{ row }">{{ row.assignedUserName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="statusColor[row.status] || 'info'" size="small">{{ statusLabel[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="escalateCount" label="督办" width="60" align="center">
          <template #default="{ row }">
            <span v-if="row.escalateCount > 0" style="color:#f56c6c;font-weight:bold">{{ row.escalateCount }}次</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="预警时间" width="155">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 'PENDING' || row.status === 'ESCALATED'" text type="success" size="small" @click="handleAccept(row)">接收</el-button>
            <el-button v-if="row.status === 'PENDING' || row.status === 'HANDLING' || row.status === 'ESCALATED'" text type="warning" size="small" @click="handleDispose(row)">处置</el-button>
            <el-button v-if="row.status === 'HANDLING' || row.status === 'ESCALATED'" text type="danger" size="small" @click="handleReject(row)">退回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>

    <!-- 预警详情抽屉 -->
    <el-drawer v-model="detailVisible" title="预警详情" size="520px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预警ID">{{ currentRow.id }}</el-descriptions-item>
        <el-descriptions-item label="预警等级"><el-tag :type="levelColor[currentRow.level]" effect="dark">{{ levelLabel[currentRow.level] }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="预警类型">{{ typeLabelMap[currentRow.alertType] || currentRow.alertType }}</el-descriptions-item>
        <el-descriptions-item label="触发来源">{{ currentRow.source === 'EVENT' ? '事件触发' : '定时扫描' }}</el-descriptions-item>
        <el-descriptions-item label="关联企业" :span="2">{{ currentRow.enterpriseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预警内容" :span="2">{{ currentRow.title }}</el-descriptions-item>
        <el-descriptions-item label="详细说明" :span="2">{{ currentRow.content }}</el-descriptions-item>
        <el-descriptions-item label="派单人员">{{ currentRow.assignedUserName || '未派单' }}</el-descriptions-item>
        <el-descriptions-item label="当前状态"><el-tag :type="statusColor[currentRow.status]">{{ statusLabel[currentRow.status] }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="督办升级">{{ currentRow.escalateCount > 0 ? currentRow.escalateCount + '次' : '未升级' }}</el-descriptions-item>
        <el-descriptions-item label="预警时间">{{ formatTime(currentRow.createTime) }}</el-descriptions-item>
        <el-descriptions-item v-if="currentRow.handleResult" label="处理结果" :span="2">{{ currentRow.handleResult }}</el-descriptions-item>
        <el-descriptions-item v-if="currentRow.handledBy" label="处理人">{{ currentRow.handledBy }}</el-descriptions-item>
        <el-descriptions-item v-if="currentRow.handleTime" label="处理时间">{{ formatTime(currentRow.handleTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <!-- 处置对话框 -->
    <el-dialog v-model="disposeVisible" title="预警处置" width="500px">
      <el-form :model="disposeForm" label-width="100px">
        <el-form-item label="处置方式">
          <el-select v-model="disposeForm.method" style="width: 100%">
            <el-option label="立即派单检查" value="派单检查" />
            <el-option label="电话核实" value="电话核实" />
            <el-option label="标记关注" value="标记关注" />
            <el-option label="转交处理" value="转交处理" />
            <el-option label="关闭预警" value="关闭预警" />
          </el-select>
        </el-form-item>
        <el-form-item label="处置说明">
          <el-input v-model="disposeForm.remark" type="textarea" :rows="3" placeholder="请输入处置说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="disposeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDispose">确认处置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getAlertList, handleAlert as apiHandleAlert, getAlertStats, acceptAlert as apiAcceptAlert, rejectAlert as apiRejectAlert, triggerAlertScan as apiTriggerScan } from '@/api/alert'

const loading = ref(false)
const total = ref(0)
const scanLoading = ref(false)
const detailVisible = ref(false)
const disposeVisible = ref(false)
const currentRow = ref({})
const disposeForm = reactive({ method: '', remark: '' })
const stats = ref({})

const levelColor = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info', ESCALATED: 'danger' }
const levelLabel = { HIGH: '高', MEDIUM: '中', LOW: '低' }
const statusColor = { PENDING: 'warning', HANDLING: '', HANDLED: 'success', ESCALATED: 'danger' }
const statusLabel = { PENDING: '待处理', HANDLING: '处理中', HANDLED: '已处理', ESCALATED: '已督办' }
const typeLabelMap = {
  LICENSE_EXPIRING: '许可证即将到期',
  LICENSE_EXPIRED: '许可证已过期',
  RECTIFICATION_OVERDUE: '整改超期未反馈',
  INSPECTION_OVERDUE: '检查任务超期',
  REPORT_MISSING: '合规报告未提交',
  REGISTRATION_ANOMALY: '注册备案异常',
  CREDIT_ANOMALY: '信用异常',
  REPEAT_VIOLATION: '屡次违规'
}
const alertTypeOptions = Object.entries(typeLabelMap).map(([value, label]) => ({ value, label }))

const queryParams = reactive({ level: '', status: '', type: '', page: 1, size: 10 })
const tableData = ref([])
const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.level) params.level = queryParams.level
    if (queryParams.status) params.status = queryParams.status
    if (queryParams.type) params.type = queryParams.type
    const res = await getAlertList(params)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const fetchStats = async () => {
  try {
    const res = await getAlertStats()
    stats.value = res.data || {}
  } catch { /* ignore */ }
}

const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.level = ''; queryParams.status = ''; queryParams.type = ''; handleSearch() }
const filterByStatus = (s) => { queryParams.status = s; handleSearch() }
const filterByLevel = (l) => { queryParams.level = l; handleSearch() }

const handleView = (row) => { currentRow.value = row; detailVisible.value = true }

const handleAccept = async (row) => {
  try {
    await apiAcceptAlert(row.id)
    ElMessage.success('已接收预警')
    fetchData()
  } catch { ElMessage.error('接收失败') }
}

const handleReject = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入退回原因', '退回预警', {
      confirmButtonText: '退回',
      cancelButtonText: '取消',
      inputPlaceholder: '退回原因（必填）',
      inputValidator: (v) => (v && v.trim() ? true : '退回原因不能为空')
    })
    await apiRejectAlert(row.id, value.trim())
    ElMessage.success('已退回，等待重新分配')
    fetchData()
    fetchStats()
  } catch (e) {
    if (e !== 'cancel' && e?.message !== 'cancel') ElMessage.error('退回失败')
  }
}

const handleDispose = (row) => {
  currentRow.value = row
  disposeForm.method = ''
  disposeForm.remark = ''
  disposeVisible.value = true
}

const submitDispose = async () => {
  if (!disposeForm.method) { ElMessage.warning('请选择处置方式'); return }
  try {
    await apiHandleAlert(currentRow.value.id, { handleResult: `[${disposeForm.method}] ${disposeForm.remark}` })
    ElMessage.success('处置成功')
    disposeVisible.value = false
    fetchData()
    fetchStats()
  } catch { ElMessage.error('处置失败') }
}

const triggerScan = async () => {
  scanLoading.value = true
  try {
    const res = await apiTriggerScan()
    const count = res.data?.newAlerts || 0
    ElMessage.success(`扫描完成，新增${count}条预警`)
    fetchData()
    fetchStats()
  } catch { ElMessage.error('扫描失败') }
  finally { scanLoading.value = false }
}

onMounted(() => { fetchData(); fetchStats() })
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
.stat-row { display: flex; gap: 12px; margin-bottom: 16px; }
.stat-item { flex: 1; background: #fff; border-radius: 8px; padding: 16px; text-align: center; cursor: pointer; box-shadow: 0 1px 4px rgba(0,0,0,0.06); transition: all 0.2s; }
.stat-item:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.stat-num { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.stat-item.pending .stat-num { color: #e6a23c; }
.stat-item.handling .stat-num { color: #409eff; }
.stat-item.handled .stat-num { color: #67c23a; }
.stat-item.high .stat-num { color: #f56c6c; }
</style>
