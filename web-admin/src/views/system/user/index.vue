<template>
  <div class="page-container">
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="用户名/姓名" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="queryParams.userType" placeholder="全部" clearable style="width: 120px">
            <el-option label="管理员" value="admin" />
            <el-option label="执法人员" value="inspector" />
            <el-option label="企业用户" value="enterprise" />
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
          <el-button type="primary" size="small" @click="openDialog()"><el-icon><Plus /></el-icon>新增用户</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="userType" label="用户类型" width="100">
          <template #default="{ row }"><el-tag size="small" :type="row.userType === 'admin' ? 'danger' : row.userType === 'inspector' ? '' : 'success'">{{ typeLabel[row.userType] || row.userType }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="165">
          <template #default="{ row }">{{ formatTime(row.createdAt || row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button text type="warning" size="small" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button text type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="form.userType" style="width:100%">
            <el-option label="管理员" value="admin" />
            <el-option label="执法人员" value="inspector" />
            <el-option label="企业用户" value="enterprise" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
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
import { getUserList, updateUser, deleteUser, resetUserPassword } from '@/api/system'

const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const typeLabel = { admin: '管理员', inspector: '执法人员', enterprise: '企业用户' }
const queryParams = reactive({ keyword: '', userType: '', page: 1, size: 10 })
const tableData = ref([])
const form = reactive({ id: null, username: '', realName: '', phone: '', userType: 'inspector', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.keyword) params.keyword = queryParams.keyword
    const res = await getUserList(params)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.keyword = ''; queryParams.userType = ''; handleSearch() }

const openDialog = (row) => {
  if (row) {
    isEdit.value = true
    form.id = row.id; form.username = row.username; form.realName = row.realName; form.phone = row.phone; form.userType = row.userType; form.password = ''
  } else {
    isEdit.value = false
    form.id = null; form.username = ''; form.realName = ''; form.phone = ''; form.userType = 'inspector'; form.password = ''
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) {
      await updateUser(form.id, { realName: form.realName, phone: form.phone, userType: form.userType })
      ElMessage.success('更新成功')
    } else {
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

const handleStatusChange = async (row) => {
  try {
    await updateUser(row.id, { status: row.status })
    ElMessage.success('状态已更新')
  } catch { row.status = row.status === 1 ? 0 : 1; ElMessage.error('操作失败') }
}

const handleResetPwd = async (row) => {
  try {
    await ElMessageBox.confirm(`确定重置用户 ${row.username} 的密码为 123456？`, '重置密码', { type: 'warning' })
    await resetUserPassword(row.id)
    ElMessage.success('密码已重置为 123456')
  } catch { /* cancelled */ }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除用户 ${row.username}？`, '删除确认', { type: 'danger' })
    await deleteUser(row.id)
    ElMessage.success('已删除')
    fetchData()
  } catch { /* cancelled */ }
}

onMounted(() => fetchData())
</script>

<style lang="scss" scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
