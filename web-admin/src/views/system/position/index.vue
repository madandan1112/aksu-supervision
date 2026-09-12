<template>
  <div class="page-container">
    <el-card shadow="never">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
        <h3 style="margin:0;">岗位管理</h3>
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增岗位</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" size="small">
        <el-table-column prop="positionCode" label="岗位编码" width="120" />
        <el-table-column prop="positionName" label="岗位名称" width="150" />
        <el-table-column prop="category" label="类别" width="100">
          <template #default="{ row }">
            <el-tag :type="categoryTagType(row.category)" size="small">{{ categoryLabel(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="80" align="center" />
<el-table-column prop="roleName" label="关联角色" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.roleName" type="warning" size="small">{{ row.roleName }}</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="薪资范围" width="180">
          <template #default="{ row }">
            <span v-if="row.salaryRangeMin">{{ row.salaryRangeMin }} - {{ row.salaryRangeMax }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row)">
              <template #reference><el-button type="danger" link size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="岗位编码" prop="positionCode"><el-input v-model="form.positionCode" maxlength="50" /></el-form-item>
        <el-form-item label="岗位名称" prop="positionName"><el-input v-model="form.positionName" maxlength="100" /></el-form-item>
        <el-form-item label="类别">
          <el-select v-model="form.category" style="width:100%">
            <el-option label="领导" value="LEADERSHIP" /><el-option label="技术" value="TECHNICAL" /><el-option label="管理" value="MANAGEMENT" /><el-option label="行政" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别"><el-input-number v-model="form.level" :min="1" :max="20" style="width:100%" /></el-form-item>
        <el-form-item label="所属组织">
          <el-input v-model="form.orgId" placeholder="组织ID" />
        </el-form-item>
        <el-form-item label="关联角色">
          <el-select v-model="form.roleId" clearable style="width:100%" placeholder="请选择关联角色">
            <el-option v-for="role in roleOptions" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="最低薪资"><el-input-number v-model="form.salaryRangeMin" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="最高薪资"><el-input-number v-model="form.salaryRangeMax" :precision="2" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="排序号"><el-input-number v-model="form.sortOrder" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">禁用</el-radio></el-radio-group></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" maxlength="500" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPositionList, createPosition, updatePosition, deletePosition, getRoleList } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const roleOptions = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)

const categoryLabel = (c) => ({ LEADERSHIP: '领导', TECHNICAL: '技术', MANAGEMENT: '管理', ADMIN: '行政' }[c] || c || '-')
const categoryTagType = (c) => ({ LEADERSHIP: 'danger', TECHNICAL: '', MANAGEMENT: 'warning', ADMIN: 'info' }[c] || 'info')

const getDefaultForm = () => ({ positionCode: '', positionName: '', category: 'TECHNICAL', level: 1, orgId: null, salaryRangeMin: null, salaryRangeMax: null, sortOrder: 0, status: 1, roleId: null, description: '' })
const form = ref(getDefaultForm())
const formRules = {
  positionCode: [{ required: true, message: '请输入岗位编码', trigger: 'blur' }],
  positionName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }]
}

const loadList = async () => {
  loading.value = true
  try {
    const [res, roleRes] = await Promise.all([
      getPositionList({ page: 1, size: 999 }),
      getRoleList()
    ])
    tableData.value = res.data?.list || res.data?.content || res.data || []
    roleOptions.value = roleRes.data?.list || roleRes.data || []
  }
  catch (e) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = null; form.value = getDefaultForm(); dialogTitle.value = '新增岗位'; dialogVisible.value = true }
const handleEdit = (row) => { isEdit.value = true; editId.value = row.id; form.value = { positionCode: row.positionCode, positionName: row.positionName, category: row.category || 'TECHNICAL', level: row.level || 1, orgId: row.orgId, salaryRangeMin: row.salaryRangeMin, salaryRangeMax: row.salaryRangeMax, sortOrder: row.sortOrder || 0, status: row.status ?? 1, roleId: row.roleId || null, description: row.description || '' }; dialogTitle.value = '编辑岗位'; dialogVisible.value = true }

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false); if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) { await updatePosition(editId.value, form.value); ElMessage.success('更新成功') }
    else { await createPosition(form.value); ElMessage.success('创建成功') }
    dialogVisible.value = false; loadList()
  } catch (e) { ElMessage.error(e.response?.data?.message || '操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try { await deletePosition(row.id); ElMessage.success('删除成功'); loadList() }
  catch (e) { ElMessage.error('删除失败') }
}

onMounted(() => { loadList() })
</script>
