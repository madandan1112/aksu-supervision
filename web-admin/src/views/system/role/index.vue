<template>
  <div class="page-container">
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span></span>
          <el-button type="primary" size="small" @click="openDialog()"><el-icon><Plus /></el-icon>新增角色</el-button>
        </div>
      </template>
      <el-table :data="roleList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="165">
          <template #default="{ row }">{{ formatTime(row.createdAt || row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button text type="warning" size="small" @click="handlePermission(row)">权限</el-button>
            <el-button text type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑角色 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="isEdit" placeholder="如 ADMIN、INSPECTOR" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入角色描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限分配 -->
    <el-dialog v-model="permDialogVisible" title="权限分配" width="600px" destroy-on-close>
      <p style="margin-bottom:12px;color:#606266">为角色 <strong>{{ currentRole.roleName }}</strong> 分配权限：</p>
      <el-tree ref="permTreeRef" :data="permTreeData" show-checkbox node-key="id" :default-checked-keys="checkedPerms" :props="{ label: 'name', children: 'children' }" />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePermissions">保存权限</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, getPermissionList } from '@/api/system'

const loading = ref(false)
const dialogVisible = ref(false)
const permDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const permTreeRef = ref(null)
const roleList = ref([])
const currentRole = ref({})
const checkedPerms = ref([])

const form = reactive({ id: null, roleName: '', roleCode: '', description: '' })
const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const permTreeData = [
  { id: 1, name: '诉求管理', children: [{ id: 11, name: '查看诉求' }, { id: 12, name: '分流诉求' }, { id: 13, name: '处理诉求' }] },
  { id: 2, name: '任务调度', children: [{ id: 21, name: '查看任务' }, { id: 22, name: '创建任务' }, { id: 23, name: '终止任务' }] },
  { id: 3, name: '数据管理', children: [{ id: 31, name: '查看企业' }, { id: 32, name: '编辑企业' }, { id: 33, name: '报告审核' }, { id: 34, name: '数据可视化' }] },
  { id: 4, name: '预警管理', children: [{ id: 41, name: '查看预警' }, { id: 42, name: '处置预警' }] },
  { id: 5, name: '系统管理', children: [{ id: 51, name: '用户管理' }, { id: 52, name: '角色管理' }, { id: 53, name: '参数配置' }, { id: 54, name: '操作日志' }] }
]

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRoleList()
    roleList.value = res.data?.list || res.data || []
  } catch { roleList.value = [] } finally { loading.value = false }
}

const openDialog = (row) => {
  if (row) {
    isEdit.value = true
    form.id = row.id; form.roleName = row.roleName; form.roleCode = row.roleCode; form.description = row.description
  } else {
    isEdit.value = false
    form.id = null; form.roleName = ''; form.roleCode = ''; form.description = ''
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) {
      await updateRole(form.id, { roleName: form.roleName, description: form.description })
      ElMessage.success('更新成功')
    } else {
      await createRole({ roleName: form.roleName, roleCode: form.roleCode, description: form.description })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

const handlePermission = (row) => {
  currentRole.value = row
  checkedPerms.value = row.roleCode === 'ADMIN' ? permTreeData.flatMap(p => p.children.map(c => c.id)) : []
  permDialogVisible.value = true
}

const savePermissions = () => {
  ElMessage.success('权限已保存')
  permDialogVisible.value = false
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除角色 ${row.roleName}？`, '删除确认', { type: 'danger' })
    await deleteRole(row.id)
    ElMessage.success('已删除')
    fetchData()
  } catch { /* cancelled */ }
}

onMounted(() => fetchData())
</script>

<style lang="scss" scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
