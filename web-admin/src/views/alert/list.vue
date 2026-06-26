<template>
  <div class="page-container">
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="等级">
          <el-select v-model="queryParams.level" placeholder="全部" clearable style="width: 120px">
            <el-option label="高" value="HIGH" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="低" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="HANDLING" />
            <el-option label="已处理" value="HANDLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 140px">
            <el-option label="特种设备异常" value="特种设备异常" />
            <el-option label="高风险诉求" value="高风险诉求" />
            <el-option label="许可证到期" value="许可证到期" />
            <el-option label="超期未检" value="超期未检" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="level" label="等级" width="80">
          <template #default="{ row }">
            <el-tag :type="levelColor[row.level] || 'info'" size="small">{{ levelLabel[row.level] || row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="预警类型" width="120" />
        <el-table-column prop="title" label="预警内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="enterpriseName" label="关联企业" width="140" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'HANDLED' ? 'success' : row.status === 'HANDLING' ? '' : 'warning'" size="small">{{ alertStatusLabel[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="预警时间" width="165">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status !== 'HANDLED'" text type="warning" size="small" @click="handleDispose(row)">处置</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>

    <!-- 预警详情 -->
    <el-dialog v-model="detailVisible" title="预警详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预警ID">{{ currentRow.id }}</el-descriptions-item>
        <el-descriptions-item label="预警等级"><el-tag :type="levelColor[currentRow.level]">{{ levelLabel[currentRow.level] }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="预警类型">{{ currentRow.type }}</el-descriptions-item>
        <el-descriptions-item label="关联企业">{{ currentRow.enterpriseName }}</el-descriptions-item>
        <el-descriptions-item label="预警内容" :span="2">{{ currentRow.title }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">{{ alertStatusLabel[currentRow.status] }}</el-descriptions-item>
        <el-descriptions-item label="预警时间">{{ formatTime(currentRow.createTime) }}</el-descriptions-item>
        <el-descriptions-item v-if="currentRow.handleResult" label="处理结果" :span="2">{{ currentRow.handleResult }}</el-descriptions-item>
      </el-descriptions>
      <template #footer><el-button @click="detailVisible = false">关闭</el-button></template>
    </el-dialog>

    <!-- 处置对话框 -->
    <el-dialog v-model="disposeVisible" title="预警处置" width="500px">
      <el-form :model="disposeForm" label-width="100px">
        <el-form-item label="处置方式">
          <el-select v-model="disposeForm.method" style="width: 100%">
            <el-option label="立即派单检查" value="派单检查" />
            <el-option label="电话核实" value="电话核实" />
            <el-option label="标记关注" value="标记关注" />
            <el-option label="转交处理" value="转交处理" />
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
import { ElMessage } from 'element-plus'
import { getAlertList, handleAlert, getAlertStats } from '@/api/alert'

const loading = ref(false)
const total = ref(0)
const detailVisible = ref(false)
const disposeVisible = ref(false)
const currentRow = ref({})
const disposeForm = reactive({ method: '', remark: '' })

const levelColor = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
const levelLabel = { HIGH: '高', MEDIUM: '中', LOW: '低' }
const alertStatusLabel = { PENDING: '待处理', HANDLING: '处理中', HANDLED: '已处理' }

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

const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.level = ''; queryParams.status = ''; queryParams.type = ''; handleSearch() }

const handleView = (row) => { currentRow.value = row; detailVisible.value = true }

const handleDispose = (row) => {
  currentRow.value = row
  disposeForm.method = ''
  disposeForm.remark = ''
  disposeVisible.value = true
}

const submitDispose = async () => {
  if (!disposeForm.method) { ElMessage.warning('请选择处置方式'); return }
  try {
    await handleAlert(currentRow.value.id, { handleResult: `[${disposeForm.method}] ${disposeForm.remark}` })
    ElMessage.success('处置成功')
    disposeVisible.value = false
    fetchData()
  } catch { ElMessage.error('处置失败') }
}

onMounted(() => fetchData())
</script>
