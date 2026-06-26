<template>
  <div class="page-container">
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="分组">
          <el-select v-model="queryParams.configGroup" placeholder="全部" clearable style="width: 140px">
            <el-option label="基础配置" value="基础配置" />
            <el-option label="业务配置" value="业务配置" />
            <el-option label="检查模板" value="检查模板" />
            <el-option label="整改期限" value="整改期限" />
          </el-select>
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
          <el-button type="primary" size="small" @click="openDialog()"><el-icon><Plus /></el-icon>新增配置</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="configKey" label="参数键" min-width="200" show-overflow-tooltip />
        <el-table-column prop="configValue" label="参数值" width="140" />
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
        <el-table-column prop="configGroup" label="分组" width="100">
          <template #default="{ row }"><el-tag size="small" :type="groupColor[row.configGroup] || ''">{{ row.configGroup || '-' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="165">
          <template #default="{ row }">{{ formatTime(row.createdAt || row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button text type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>

    <!-- 新增/编辑配置 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑配置' : '新增配置'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="参数键" prop="configKey">
          <el-input v-model="form.configKey" :disabled="isEdit" placeholder="如 inspection.deadline.days" />
        </el-form-item>
        <el-form-item label="参数值" prop="configValue">
          <el-input v-model="form.configValue" placeholder="请输入参数值" />
        </el-form-item>
        <el-form-item label="分组" prop="configGroup">
          <el-select v-model="form.configGroup" style="width:100%">
            <el-option label="基础配置" value="基础配置" />
            <el-option label="业务配置" value="业务配置" />
            <el-option label="检查模板" value="检查模板" />
            <el-option label="整改期限" value="整改期限" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getConfigList, createConfig, updateConfig, deleteConfig } from '@/api/system'

const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const groupColor = { '基础配置': '', '业务配置': 'success', '检查模板': 'warning', '整改期限': 'danger' }

const queryParams = reactive({ configGroup: '', page: 1, size: 10 })
const tableData = ref([])
const form = reactive({ id: null, configKey: '', configValue: '', configGroup: '', description: '' })
const rules = {
  configKey: [{ required: true, message: '请输入参数键', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入参数值', trigger: 'blur' }],
  configGroup: [{ required: true, message: '请选择分组', trigger: 'change' }]
}

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.configGroup) params.configGroup = queryParams.configGroup
    const res = await getConfigList(params)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.configGroup = ''; handleSearch() }

const openDialog = (row) => {
  if (row) {
    isEdit.value = true
    form.id = row.id; form.configKey = row.configKey; form.configValue = row.configValue; form.configGroup = row.configGroup; form.description = row.description
  } else {
    isEdit.value = false
    form.id = null; form.configKey = ''; form.configValue = ''; form.configGroup = ''; form.description = ''
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) {
      await updateConfig(form.id, { configValue: form.configValue, description: form.description })
      ElMessage.success('更新成功')
    } else {
      await createConfig({ configKey: form.configKey, configValue: form.configValue, description: form.description, configGroup: form.configGroup })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除配置 ${row.configKey}？`, '删除确认', { type: 'danger' })
    await deleteConfig(row.id)
    ElMessage.success('已删除')
    fetchData()
  } catch { /* cancelled */ }
}

onMounted(() => fetchData())
</script>

<style lang="scss" scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
