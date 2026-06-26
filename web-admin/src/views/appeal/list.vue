<template>
  <div class="page-container">
    <!-- 筛选 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="待分流" value="PENDING" />
            <el-option label="已分配" value="ASSIGNED" />
            <el-option label="处理中" value="HANDLING" />
            <el-option label="已办结" value="HANDLED" />
            <el-option label="已评价" value="EVALUATED" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 140px">
            <el-option label="政策咨询" value="政策咨询" />
            <el-option label="许可办理" value="许可办理" />
            <el-option label="检查整改" value="检查整改" />
            <el-option label="跨部门协调" value="跨部门协调" />
            <el-option label="质量投诉" value="质量投诉" />
            <el-option label="安全举报" value="安全举报" />
            <el-option label="服务建议" value="服务建议" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="标题/企业名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column prop="enterpriseName" label="企业" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">{{ row.enterpriseName || '企业#' + row.enterpriseId }}</template>
        </el-table-column>
        <el-table-column prop="appealType" label="诉求类型" width="110">
          <template #default="{ row }">
            <el-tag size="small" :type="appealTypeColor[row.appealType] || ''">{{ row.appealType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="诉求标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTypeMap[row.status] || 'info'" size="small">{{ statusLabel[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignedTo" label="处理人" width="90">
          <template #default="{ row }">{{ row.assignedTo || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="165">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 'PENDING'" text type="warning" size="small" @click="handleAssign(row)">分流</el-button>
            <el-button v-if="row.status === 'ASSIGNED' || row.status === 'HANDLING'" text type="success" size="small" @click="handleView(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>

    <!-- 分流对话框 -->
    <el-dialog v-model="assignDialogVisible" title="诉求分流" width="600px" destroy-on-close>
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="100px">
        <el-form-item label="诉求编号">
          <el-input :model-value="String(currentRow?.id || '')" disabled />
        </el-form-item>
        <el-form-item label="初筛建议">
          <el-tag type="info">{{ suggestDept(currentRow) }}</el-tag>
        </el-form-item>
        <el-form-item label="分配科室" prop="assignedDept">
          <el-select v-model="assignForm.assignedDept" placeholder="请选择科室" style="width: 100%">
            <el-option label="食品安全监管科" value="食品安全监管科" />
            <el-option label="特种设备安全监察科" value="特种设备安全监察科" />
            <el-option label="产品质量安全监管科" value="产品质量安全监管科" />
            <el-option label="知识产权科" value="知识产权科" />
            <el-option label="行政许可科" value="行政许可科" />
            <el-option label="综合执法科" value="综合执法科" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理人" prop="assignedTo">
          <el-input v-model="assignForm.assignedTo" placeholder="请输入处理人姓名" />
        </el-form-item>
        <el-form-item label="分流意见">
          <el-input v-model="assignForm.remark" type="textarea" :rows="3" placeholder="请输入分流意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确认分流</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAppealList, assignAppeal } from '@/api/appeal'

const router = useRouter()
const loading = ref(false)
const total = ref(0)
const assignDialogVisible = ref(false)
const currentRow = ref(null)
const assignFormRef = ref(null)

const statusTypeMap = { PENDING: 'warning', ASSIGNED: 'primary', HANDLING: '', HANDLED: 'success', EVALUATED: 'info' }
const statusLabel = { PENDING: '待分流', ASSIGNED: '已分配', HANDLING: '处理中', HANDLED: '已办结', EVALUATED: '已评价' }
const appealTypeColor = { '政策咨询': '', '许可办理': 'success', '检查整改': 'warning', '跨部门协调': 'danger', '质量投诉': 'danger', '安全举报': 'danger', '服务建议': 'info' }

const queryParams = reactive({ status: '', type: '', keyword: '', page: 1, size: 10 })
const assignForm = reactive({ assignedTo: '', assignedDept: '', remark: '' })
const assignRules = {
  assignedDept: [{ required: true, message: '请选择科室', trigger: 'change' }],
  assignedTo: [{ required: true, message: '请输入处理人', trigger: 'blur' }]
}

const tableData = ref([])

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const suggestDept = (row) => {
  if (!row) return '-'
  const map = { '食品安全': '食品安全监管科', '特种设备': '特种设备安全监察科', '产品质量': '产品质量安全监管科', '知识产权': '知识产权科', '许可办理': '行政许可科', '政策咨询': '行政许可科' }
  for (const [key, val] of Object.entries(map)) {
    if (row.appealType?.includes(key) || row.title?.includes(key)) return val
  }
  return '综合执法科'
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.status) params.status = queryParams.status
    if (queryParams.type) params.type = queryParams.type
    if (queryParams.keyword) params.keyword = queryParams.keyword
    const res = await getAppealList(params)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.status = ''; queryParams.type = ''; queryParams.keyword = ''; handleSearch() }
const handleView = (row) => { router.push(`/appeal/${row.id}`) }

const handleAssign = (row) => {
  currentRow.value = row
  assignForm.assignedTo = ''
  assignForm.assignedDept = suggestDept(row)
  assignForm.remark = ''
  assignDialogVisible.value = true
}

const submitAssign = async () => {
  const valid = await assignFormRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await assignAppeal(currentRow.value.id, { assignedTo: assignForm.assignedTo, remark: `[${assignForm.assignedDept}] ${assignForm.remark}` })
    ElMessage.success('分流成功')
    assignDialogVisible.value = false
    fetchData()
  } catch (e) { ElMessage.error('分流失败') }
}

onMounted(() => fetchData())
</script>

<style lang="scss" scoped></style>
