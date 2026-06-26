<template>
  <div class="page-container">
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="用户名/姓名/手机号" clearable style="width: 200px" />
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
          <div class="header-stats">
            <el-tag type="info">小程序用户总数：{{ totalCount }}</el-tag>
          </div>
          <el-button type="primary" size="small" @click="openDialog()">
            <el-icon><Plus /></el-icon>新增小程序用户
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="realName" label="姓名" width="120">
          <template #default="{ row }">{{ row.realName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140">
          <template #default="{ row }">{{ row.phone || '-' }}</template>
        </el-table-column>
        <el-table-column prop="userType" label="用户类型" width="120">
          <template #default="{ row }">
            <el-tag size="small" type="success">{{ typeLabel[row.userType] || row.userType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleViewEnterprise(row)">查看企业</el-button>
            <el-button text type="warning" size="small" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button text type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 新增小程序用户对话框 -->
    <el-dialog v-model="dialogVisible" title="新增小程序用户" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="form.userType" style="width:100%">
            <el-option label="企业用户" value="enterprise_user" />
            <el-option label="企业" value="enterprise" />
          </el-select>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMiniappUserList, createMiniappUser, getMiniappUserCount, resetUserPassword, deleteUser } from '@/api/system'

const router = useRouter()
const loading = ref(false)
const total = ref(0)
const totalCount = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)

const typeLabel = { enterprise_user: '企业用户', enterprise: '企业' }
const queryParams = reactive({ keyword: '', page: 1, size: 10 })
const tableData = ref([])
const form = reactive({ username: '', password: '', realName: '', phone: '', userType: 'enterprise_user' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码不少于6位', trigger: 'blur' }],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
}

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.keyword) params.keyword = queryParams.keyword
    const res = await getMiniappUserList(params)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const fetchCount = async () => {
  try {
    const res = await getMiniappUserCount()
    totalCount.value = res.data?.total || 0
  } catch { /* ignore */ }
}

const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.keyword = ''; handleSearch() }

const openDialog = () => {
  form.username = ''; form.password = ''; form.realName = ''; form.phone = ''; form.userType = 'enterprise_user'
  dialogVisible.value = true
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await createMiniappUser(form)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    fetchData()
    fetchCount()
  } catch { ElMessage.error('创建失败') }
}

const handleViewEnterprise = (row) => {
  // 跳转到后台管理-企业详情页，通过userId查看
  router.push({ path: `/backend/enterprise`, query: { userId: row.id, username: row.username } })
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
    await ElMessageBox.confirm(`确定删除小程序用户 ${row.username}？`, '删除确认', { type: 'danger' })
    await deleteUser(row.id)
    ElMessage.success('已删除')
    fetchData()
    fetchCount()
  } catch { /* cancelled */ }
}

onMounted(() => { fetchData(); fetchCount() })
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-stats {
    display: flex;
    gap: 12px;
    align-items: center;
  }
}
</style>
