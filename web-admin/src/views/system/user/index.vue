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
        <el-form-item label="所属组织">
          <el-tree-select v-model="queryParams.orgId" :data="orgTreeData" :props="{ label: 'name', value: 'id', children: 'children' }" placeholder="全部" clearable check-strictly filterable style="width: 200px" />
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
        <el-table-column prop="username" label="用户名" width="110" />
        <el-table-column prop="realName" label="姓名" width="90" />
        <el-table-column prop="phone" label="手机号" width="125" />
        <el-table-column prop="orgName" label="所属组织" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">{{ row.orgName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="jobTitleName" label="职务" width="110">
          <template #default="{ row }">{{ row.jobTitleName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="positionName" label="岗位" width="110">
          <template #default="{ row }">{{ row.positionName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="userType" label="用户类型" width="90">
          <template #default="{ row }"><el-tag size="small" :type="row.userType === 'ADMIN' ? 'danger' : row.userType === 'INSPECTOR' ? '' : 'success'">{{ typeLabel[row.userType] || row.userType }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" size="small" />
          </template>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="600px" destroy-on-close>
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
            <el-option label="管理员" value="ADMIN" />
            <el-option label="执法人员" value="INSPECTOR" />
            <el-option label="企业用户" value="ENTERPRISE" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属组织" prop="orgId">
          <el-tree-select v-model="form.orgId" :data="orgTreeData" :props="{ label: 'name', value: 'id', children: 'children' }" placeholder="请选择所属科室/组织" clearable check-strictly filterable style="width:100%" />
        </el-form-item>
        <el-form-item label="职务" prop="jobTitleId">
          <el-select v-model="form.jobTitleId" placeholder="请选择职务" clearable filterable style="width:100%">
            <el-option v-for="item in jobTitleList" :key="item.id" :label="item.titleName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="岗位" prop="positionId">
          <el-select v-model="form.positionId" placeholder="请选择岗位" clearable filterable style="width:100%">
            <el-option v-for="item in positionList" :key="item.id" :label="item.positionName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="执法证号">
          <el-input v-model="form.lawEnforcementNo" placeholder="请输入执法证号" />
        </el-form-item>
        <el-form-item label="头像照片">
          <div class="user-photo-box">
            <el-image v-if="form.photoUrl" :src="form.photoUrl" fit="cover" class="user-photo-img" />
            <div v-else class="user-photo-empty">未上传</div>
            <div class="user-photo-actions">
              <el-button size="small" type="primary" @click="triggerFileUpload('photo')">更换</el-button>
              <el-button v-if="form.photoUrl" size="small" type="danger" @click="form.photoUrl = ''">删除</el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 隐藏文件上传 -->
    <input type="file" ref="fileInputRef" accept="image/*" @change="handleFileUpload" style="display:none" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUser, deleteUser, resetUserPassword, getOrgTree, getAllJobTitles, getAllPositions } from '@/api/system'

const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

// 下拉选择数据
const orgTreeData = ref([])
const jobTitleList = ref([])
const positionList = ref([])

const typeLabel = { ADMIN: '管理员', INSPECTOR: '执法人员', ENTERPRISE: '企业用户', admin: '管理员', inspector: '执法人员', enterprise: '企业用户' }
const queryParams = reactive({ keyword: '', userType: '', orgId: null, page: 1, size: 10 })
const tableData = ref([])
const form = reactive({ id: null, username: '', realName: '', phone: '', userType: 'inspector', password: '', orgId: null, jobTitleId: null, positionId: null, lawEnforcementNo: '', photoUrl: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

// 加载下拉数据
const loadDropdownData = async () => {
  try {
    const [orgRes, jobRes, posRes] = await Promise.all([
      getOrgTree(),
      getAllJobTitles(),
      getAllPositions()
    ])
    orgTreeData.value = orgRes.data || []
    jobTitleList.value = jobRes.data || []
    positionList.value = posRes.data || []
  } catch (e) {
    console.error('加载下拉数据失败', e)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.keyword) params.keyword = queryParams.keyword
    if (queryParams.userType) params.userType = queryParams.userType
    const res = await getUserList(params)
    let list = res.data?.list || []
    // 前端按orgId筛选（后端暂不支持orgId参数过滤）
    if (queryParams.orgId) {
      list = list.filter(u => u.orgId === queryParams.orgId)
    }
    tableData.value = list
    total.value = res.data?.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.keyword = ''; queryParams.userType = ''; queryParams.orgId = null; handleSearch() }

const openDialog = (row) => {
  if (row) {
    isEdit.value = true
    form.id = row.id; form.username = row.username; form.realName = row.realName
    form.phone = row.phone; form.userType = row.userType; form.password = ''
    form.orgId = row.orgId || null; form.jobTitleId = row.jobTitleId || null; form.positionId = row.positionId || null
    form.lawEnforcementNo = row.lawEnforcementNo || ''
    form.photoUrl = row.photoUrl || ''
  } else {
    isEdit.value = false
    form.id = null; form.username = ''; form.realName = ''; form.phone = ''
    form.userType = 'inspector'; form.password = ''; form.orgId = null; form.jobTitleId = null; form.positionId = null
    form.lawEnforcementNo = ''; form.photoUrl = ''
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) {
      const data = { realName: form.realName, phone: form.phone, userType: form.userType }
      if (form.orgId) data.orgId = form.orgId
      if (form.jobTitleId) data.jobTitleId = form.jobTitleId
      if (form.positionId) data.positionId = form.positionId
      data.lawEnforcementNo = form.lawEnforcementNo
      data.photoUrl = form.photoUrl
      await updateUser(form.id, data)
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

// 文件上传
const fileInputRef = ref(null)
const currentUploadType = ref('')
const uploadLoading = ref(false)

const triggerFileUpload = (type) => {
  currentUploadType.value = type
  fileInputRef.value?.click()
}

const handleFileUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  uploadLoading.value = true
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await fetch('/api/file/upload', { method: 'POST', body: fd })
    const json = await res.json()
    const url = json.data?.url || json.url || json.data || ''
    if (!url) { ElMessage.error('上传失败'); return }
    if (currentUploadType.value === 'photo') form.photoUrl = url
    ElMessage.success('上传成功')
  } catch { ElMessage.error('上传失败') }
  e.target.value = ''
  uploadLoading.value = false
}

onMounted(() => {
  loadDropdownData()
  fetchData()
})
</script>

<style lang="scss" scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
