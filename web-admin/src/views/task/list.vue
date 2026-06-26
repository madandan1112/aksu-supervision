<template>
  <div class="page-container">
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="待执行" value="PENDING" />
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已终止" value="TERMINATED" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.taskType" placeholder="全部" clearable style="width: 140px">
            <el-option label="日常检查" value="日常检查" />
            <el-option label="专项检查" value="专项检查" />
            <el-option label="投诉核查" value="投诉核查" />
            <el-option label="随机抽查" value="随机抽查" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="标题/描述" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span></span>
          <el-button type="primary" @click="$router.push('/task/create')">
            <el-icon><Plus /></el-icon>创建任务
          </el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column prop="taskType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="taskTypeColor[row.taskType] || ''">{{ row.taskType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="任务标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTypeMap[row.status] || 'info'" size="small">{{ statusLabel[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdBy" label="创建人" width="90" />
        <el-table-column prop="plannedStartTime" label="计划开始" width="165">
          <template #default="{ row }">{{ formatTime(row.plannedStartTime) }}</template>
        </el-table-column>
        <el-table-column prop="plannedEndTime" label="计划结束" width="165">
          <template #default="{ row }">{{ formatTime(row.plannedEndTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="$router.push(`/task/${row.id}`)">详情</el-button>
            <el-button v-if="row.status === 'IN_PROGRESS'" text type="danger" size="small" @click="quickTerminate(row)">终止</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>

    <!-- 快速终止 -->
    <el-dialog v-model="terminateVisible" title="终止任务" width="420px">
      <p>确定终止任务 <strong>{{ currentRow?.title }}</strong>？</p>
      <el-input v-model="terminateReason" type="textarea" :rows="2" placeholder="请输入终止原因" style="margin-top:12px" />
      <template #footer>
        <el-button @click="terminateVisible = false">取消</el-button>
        <el-button type="danger" @click="doTerminate">确认终止</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTaskList, terminateTask } from '@/api/task'

const loading = ref(false)
const total = ref(0)
const terminateVisible = ref(false)
const currentRow = ref(null)
const terminateReason = ref('')

const statusTypeMap = { PENDING: 'warning', IN_PROGRESS: '', COMPLETED: 'success', TERMINATED: 'danger' }
const statusLabel = { PENDING: '待执行', IN_PROGRESS: '进行中', COMPLETED: '已完成', TERMINATED: '已终止' }
const taskTypeColor = { '日常检查': '', '专项检查': 'warning', '投诉核查': 'danger', '随机抽查': 'success' }

const queryParams = reactive({ status: '', taskType: '', keyword: '', page: 1, size: 10 })
const tableData = ref([])

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.status) params.status = queryParams.status
    if (queryParams.taskType) params.type = queryParams.taskType
    if (queryParams.keyword) params.keyword = queryParams.keyword
    const res = await getTaskList(params)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.status = ''; queryParams.taskType = ''; queryParams.keyword = ''; handleSearch() }

const quickTerminate = (row) => { currentRow.value = row; terminateReason.value = ''; terminateVisible.value = true }

const doTerminate = async () => {
  if (!terminateReason.value) { ElMessage.warning('请输入终止原因'); return }
  try {
    await terminateTask(currentRow.value.id, { reason: terminateReason.value })
    ElMessage.success('任务已终止')
    terminateVisible.value = false
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

onMounted(() => fetchData())
</script>

<style lang="scss" scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
