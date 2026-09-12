<template>
  <div class="page-container">
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>角色权限管理</span>
          <el-button type="primary" size="small" @click="openDialog()"><el-icon><Plus /></el-icon>新增角色</el-button>
        </div>
      </template>
      <el-table :data="roleList" v-loading="loading" stripe row-key="id" default-expand-all>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="roleName" label="角色名称" width="140" />
        <el-table-column prop="roleCode" label="角色编码" width="160" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column label="数据范围" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.dataScope === 'ALL' ? 'danger' : row.dataScope === 'DEPT' ? 'warning' : 'info'">
              {{ scopeLabel(row.dataScope) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="区域权限" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.regionScope === 'ALL' || row.regionScope === 'REGION' ? 'danger' : row.regionScope === 'CITY' ? 'warning' : 'info'">
              {{ regionLabel(row.regionScope) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="科室权限" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ deptLabel(row.deptScope) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button text type="warning" size="small" @click="openPermDialog(row)">权限</el-button>
            <el-button text type="danger" size="small" @click="handleDelete(row)" :disabled="row.roleCode === 'ADMIN'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑角色 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="如：地区主管领导" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="isEdit" placeholder="如：REGION_DIRECTOR" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-divider content-position="left">数据权限配置</el-divider>
        <el-form-item label="数据范围">
          <el-select v-model="form.dataScope" placeholder="选择数据范围">
            <el-option label="全部数据" value="ALL" />
            <el-option label="本部门及下级" value="DEPT" />
            <el-option label="仅本人" value="SELF" />
          </el-select>
        </el-form-item>
        <el-form-item label="区域权限">
          <el-select v-model="form.regionScope" placeholder="选择区域权限">
            <el-option label="全地区" value="REGION" />
            <el-option label="本市" value="CITY" />
            <el-option label="本县/区" value="COUNTY" />
            <el-option label="仅自己" value="SELF" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室权限">
          <el-select v-model="form.deptScope" placeholder="选择科室权限">
            <el-option label="全部科室" value="ALL" />
            <el-option label="本科室" value="DEPT" />
            <el-option label="仅自己" value="SELF" />
          </el-select>
        </el-form-item>
        <el-form-item label="组织层级">
          <el-select v-model="form.orgLevel" placeholder="选择组织层级">
            <el-option label="地区级" value="REGION" />
            <el-option label="市级" value="CITY" />
            <el-option label="县/区级" value="COUNTY" />
            <el-option label="科室级" value="DEPT" />
            <el-option label="企业" value="ENTERPRISE" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限分配 -->
    <el-dialog v-model="permDialogVisible" title="按钮权限分配" width="700px" destroy-on-close>
      <p style="margin-bottom:12px;color:#606266">为角色 <strong>{{ currentRole.roleName }}</strong> 分配操作权限：</p>
      <div v-for="mod in permModules" :key="mod.key" style="margin-bottom:16px">
        <div style="font-weight:600;margin-bottom:8px;color:#303133">{{ mod.label }}</div>
        <el-checkbox-group v-model="checkedPerms">
          <el-checkbox v-for="action in mod.actions" :key="action.code" :label="action.code">{{ action.label }}</el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePermissions" :loading="permSaving">保存权限</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, getRolePermissions, updateRolePermissions } from '@/api/system'

const loading = ref(false)
const dialogVisible = ref(false)
const permDialogVisible = ref(false)
const permSaving = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const roleList = ref([])
const currentRole = ref({})
const checkedPerms = ref([])

const form = reactive({
  id: null, roleName: '', roleCode: '', description: '',
  dataScope: 'SELF', regionScope: 'SELF', deptScope: 'SELF', orgLevel: '', sortOrder: 0
})
const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const permModules = [
  { key: 'enterprise', label: '企业管理', actions: [
    { code: 'enterprise:view', label: '查看企业' }, { code: 'enterprise:create', label: '创建企业' },
    { code: 'enterprise:update', label: '修改企业' }, { code: 'enterprise:delete', label: '删除企业' }
  ]},
  { key: 'inspection', label: '现场检查', actions: [
    { code: 'inspection:view', label: '查看检查' }, { code: 'inspection:create', label: '创建检查' },
    { code: 'inspection:update', label: '修改检查' }, { code: 'inspection:delete', label: '删除检查' }
  ]},
  { key: 'rectification', label: '整改管理', actions: [
    { code: 'rectification:view', label: '查看整改' }, { code: 'rectification:create', label: '创建整改' },
    { code: 'rectification:update', label: '修改整改' }, { code: 'rectification:delete', label: '删除整改' }
  ]},
  { key: 'report', label: '报告管理', actions: [
    { code: 'report:view', label: '查看报告' }, { code: 'report:create', label: '创建报告' },
    { code: 'report:update', label: '修改报告' }, { code: 'report:delete', label: '删除报告' }
  ]},
  { key: 'user', label: '用户管理', actions: [
    { code: 'user:view', label: '查看用户' }, { code: 'user:create', label: '创建用户' },
    { code: 'user:update', label: '修改用户' }, { code: 'user:delete', label: '删除用户' }
  ]},
  { key: 'system', label: '系统管理', actions: [
    { code: 'system:config', label: '系统配置' }
  ]},
  { key: 'appeal', label: '诉求管理', actions: [
    { code: 'appeal:view', label: '查看诉求' }, { code: 'appeal:create', label: '创建诉求' }
  ]}
]

const scopeLabel = (v) => ({ ALL: '全部', DEPT: '部门', SELF: '本人' }[v] || v || '-')
const regionLabel = (v) => ({ ALL: '全地区', REGION: '地区', CITY: '市级', COUNTY: '县级', SELF: '本人' }[v] || v || '-')
const deptLabel = (v) => ({ ALL: '全部', DEPT: '本科室', SELF: '本人' }[v] || v || '-')

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRoleList({ page: 1, size: 100 })
    roleList.value = res.data?.list || res.data || []
  } catch { roleList.value = [] } finally { loading.value = false }
}

const openDialog = (row) => {
  if (row) {
    isEdit.value = true
    Object.assign(form, {
      id: row.id, roleName: row.roleName, roleCode: row.roleCode, description: row.description,
      dataScope: row.dataScope || 'SELF', regionScope: row.regionScope || 'SELF',
      deptScope: row.deptScope || 'SELF', orgLevel: row.orgLevel || '', sortOrder: row.sortOrder || 0
    })
  } else {
    isEdit.value = false
    Object.assign(form, {
      id: null, roleName: '', roleCode: '', description: '',
      dataScope: 'SELF', regionScope: 'SELF', deptScope: 'SELF', orgLevel: '', sortOrder: 0
    })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    const data = { ...form }
    if (isEdit.value) {
      await updateRole(form.id, data)
      ElMessage.success('更新成功')
    } else {
      await createRole(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

const openPermDialog = async (row) => {
  currentRole.value = row
  try {
    const res = await getRolePermissions(row.id)
    checkedPerms.value = (res.data || []).map(p => p.permissionCode)
  } catch { checkedPerms.value = [] }
  permDialogVisible.value = true
}

const savePermissions = async () => {
  permSaving.value = true
  try {
    await updateRolePermissions(currentRole.value.id, checkedPerms.value)
    ElMessage.success('权限已保存')
    permDialogVisible.value = false
  } catch { ElMessage.error('保存失败') } finally { permSaving.value = false }
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
