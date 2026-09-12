<template>
  <div class="page-container">
    <el-card shadow="never">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
        <h3 style="margin:0;">职务管理</h3>
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增职务</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" size="small">
        <el-table-column prop="titleCode" label="职务编码" width="120" />
        <el-table-column prop="titleName" label="职务名称" width="150" />
        <el-table-column prop="category" label="类别" width="100">
          <template #default="{ row }">
            <el-tag :type="categoryTagType(row.category)" size="small">{{ categoryLabel(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isLeadership" label="领导职务" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isLeadership === 1 ? 'danger' : 'info'" size="small">{{ row.isLeadership === 1 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="80" align="center" />
        <el-table-column prop="roleName" label="关联角色" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.roleName" type="warning" size="small">{{ row.roleName }}</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
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
        <el-form-item label="职务编码" prop="titleCode"><el-input v-model="form.titleCode" maxlength="50" /></el-form-item>
        <el-form-item label="职务名称" prop="titleName"><el-input v-model="form.titleName" maxlength="100" /></el-form-item>
        <el-form-item label="类别">
          <el-select v-model="form.category" style="width:100%">
            <el-option label="技术" value="TECHNICAL" /><el-option label="管理" value="MANAGEMENT" /><el-option label="行政" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="领导职务"><el-radio-group v-model="form.isLeadership"><el-radio :value="1">是</el-radio><el-radio :value="0">否</el-radio></el-radio-group></el-form-item>
        <el-form-item label="级别"><el-input-number v-model="form.level" :min="1" :max="20" style="width:100%" /></el-form-item>
        <el-form-item label="关联角色">
          <el-select v-model="form.roleId" clearable style="width:100%" placeholder="请选择关联角色">
            <el-option v-for="role in roleOptions" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
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
import { getJobTitleList, createJobTitle, updateJobTitle, deleteJobTitle, getRoleList } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const roleOptions = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)

const categoryLabel = (c) => ({ TECHNICAL: '技术', MANAGEMENT: '管理', ADMIN: '行政' }[c] || c || '-')
const categoryTagType = (c) => ({ TECHNICAL: '', MANAGEMENT: 'warning', ADMIN: 'info' }[c] || 'info')

const getDefaultForm = () => ({ titleCode: '', titleName: '', category: 'TECHNICAL', isLeadership: 0, level: 1, sortOrder: 0, status: 1, roleId: null, description: '' })
const form = ref(getDefaultForm())
const formRules = {
  titleCode: [{ required: true, message: '请输入职务编码', trigger: 'blur' }],
  titleName: [{ required: true, message: '请输入职务名称', trigger: 'blur' }]
}

const loadList = async () => {
  loading.value = true
  try {
    const [res, roleRes] = await Promise.all([
      getJobTitleList({ page: 1, size: 999 }),
      getRoleList()
    ])
    tableData.value = res.data?.list || res.data?.content || res.data || []
    roleOptions.value = roleRes.data?.list || roleRes.data || []
  }
  catch (e) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; editId.value = null; form.value = getDefaultForm(); dialogTitle.value = '新增职务'; dialogVisible.value = true }
const handleEdit = (row) => { isEdit.value = true; editId.value = row.id; form.value = { titleCode: row.titleCode, titleName: row.titleName, category: row.category || 'TECHNICAL', isLeadership: row.isLeadership ?? 0, level: row.level || 1, sortOrder: row.sortOrder || 0, status: row.status ?? 1, description: row.description || '' }; dialogTitle.value = '编辑职务'; dialogVisible.value = true }

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false); if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) { await updateJobTitle(editId.value, form.value); ElMessage.success('更新成功') }
    else { await createJobTitle(form.value); ElMessage.success('创建成功') }
    dialogVisible.value = false; loadList()
  } catch (e) { ElMessage.error(e.response?.data?.message || '操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try { await deleteJobTitle(row.id); ElMessage.success('删除成功'); loadList() }
  catch (e) { ElMessage.error('删除失败') }
}

onMounted(() => { loadList() })
</script>
