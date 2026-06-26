<template>
  <div class="org-container">
    <div class="org-header">
      <h3>组织架构管理</h3>
      <div class="org-actions">
        <el-button type="primary" @click="handleAdd(null)">
          <el-icon><Plus /></el-icon> 新增地区
        </el-button>
        <el-button @click="loadTree">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
    </div>

    <el-alert
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 16px"
    >
      <template #title>
        组织架构共5级：地区(1) → 县市(2) → 乡镇街道(3) → 单位(4) → 科室(5)
      </template>
    </el-alert>

    <div class="org-tree-wrapper" v-loading="loading">
      <el-tree
        ref="treeRef"
        :data="treeData"
        :props="treeProps"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
        highlight-current
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <div class="node-info">
              <el-tag
                :type="levelTagType(data.level)"
                size="small"
                effect="dark"
                class="level-tag"
              >
                {{ levelName(data.level) }}
              </el-tag>
              <span class="node-name">{{ data.name }}</span>
              <el-tag v-if="data.orgCode" size="small" effect="plain">{{ data.orgCode }}</el-tag>
              <el-tag v-if="data.orgType" type="warning" size="small" effect="plain">{{ orgTypeLabel(data.orgType) }}</el-tag>
              <span v-if="data.leaderName" class="leader-info">{{ data.leaderName }} {{ data.leaderPhone }}</span>
              <el-tag
                v-if="data.status === 0"
                type="danger"
                size="small"
                class="status-tag"
              >
                已禁用
              </el-tag>
            </div>
            <div class="node-actions">
              <el-button
                v-if="data.level < 5"
                type="primary"
                link
                size="small"
                @click.stop="handleAdd(data)"
              >
                <el-icon><Plus /></el-icon> 新增下级
              </el-button>
              <el-button
                type="primary"
                link
                size="small"
                @click.stop="handleViewMembers(data)"
              >
                <el-icon><User /></el-icon> 人员
              </el-button>
              <el-button
                type="primary"
                link
                size="small"
                @click.stop="handleEdit(data)"
              >
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-popconfirm
                :title="'确定删除【' + data.name + '】及其所有子节点吗？'"
                confirm-button-text="确定"
                cancel-button-text="取消"
                @confirm="handleDelete(data)"
              >
                <template #reference>
                  <el-button
                    type="danger"
                    link
                    size="small"
                    @click.stop
                  >
                    <el-icon><Delete /></el-icon> 删除
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </template>
      </el-tree>

      <el-empty v-if="!loading && treeData.length === 0" description="暂无组织架构数据，请点击「新增地区」创建" />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="所属上级" v-if="form.parentId">
          <el-input :model-value="parentName" disabled />
        </el-form-item>
        <el-form-item label="层级" prop="level">
          <el-select v-model="form.level" :disabled="!!form.parentId" placeholder="请选择层级" style="width: 100%">
            <el-option label="地区（第1级）" :value="1" />
            <el-option label="县市（第2级）" :value="2" />
            <el-option label="乡镇街道（第3级）" :value="3" />
            <el-option label="单位（第4级）" :value="4" />
            <el-option label="科室（第5级）" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="组织编码">
          <el-input v-model="form.orgCode" placeholder="留空自动生成" maxlength="50" />
        </el-form-item>
        <el-form-item label="组织类型">
          <el-select v-model="form.orgType" clearable style="width: 100%">
            <el-option label="企业" value="ENTERPRISE" />
            <el-option label="机关" value="GOVERNMENT" />
            <el-option label="事业单位" value="INSTITUTION" />
            <el-option label="社会团体" value="SOCIAL_GROUP" />
          </el-select>
        </el-form-item>
        <el-form-item label="信用代码">
          <el-input v-model="form.unifiedSocialCreditCode" maxlength="18" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leaderName" maxlength="100" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.leaderPhone" maxlength="20" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" maxlength="500" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="1000" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="可选描述"
            maxlength="500"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 组织人员查看 -->
    <el-dialog v-model="memberDialogVisible" width="800px" destroy-on-close>
      <template #header><span>{{ memberOrgName }} - 人员列表</span></template>
      <div style="margin-bottom:12px;display:flex;justify-content:flex-end;">
        <el-button type="primary" size="small" @click="handleAssignMember"><el-icon><Plus /></el-icon> 分配人员</el-button>
      </div>
      <el-table :data="memberList" border stripe size="small">
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="positionName" label="岗位" width="120" />
        <el-table-column prop="jobTitleName" label="职务" width="120" />
        <el-table-column label="主职" width="80" align="center">
          <template #default="{ row }"><el-tag :type="row.isPrimary === 1 ? '' : 'info'" size="small">{{ row.isPrimary === 1 ? '主职' : '兼职' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '在职' : '离职' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="startDate" label="任职日期" width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.isPrimary !== 1" type="primary" link size="small" @click="handleSetPrimary(row)">设为主职</el-button>
            <el-popconfirm title="确定移除？" @confirm="handleRemoveMember(row)">
              <template #reference><el-button type="danger" link size="small">移除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 分配人员 -->
    <el-dialog v-model="assignDialogVisible" title="分配人员到组织" width="500px" destroy-on-close>
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="100px">
        <el-form-item label="选择用户" prop="userId">
          <el-select v-model="assignForm.userId" filterable placeholder="搜索用户" style="width:100%">
            <el-option v-for="u in userOptions" :key="u.id" :label="u.realName || u.username" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="岗位">
          <el-select v-model="assignForm.positionId" clearable style="width:100%">
            <el-option v-for="p in positionOptions" :key="p.id" :label="p.positionName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职务">
          <el-select v-model="assignForm.jobTitleId" clearable style="width:100%">
            <el-option v-for="t in jobTitleOptions" :key="t.id" :label="t.titleName" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否主职"><el-radio-group v-model="assignForm.isPrimary"><el-radio :value="1">主职</el-radio><el-radio :value="0">兼职</el-radio></el-radio-group></el-form-item>
        <el-form-item label="任职日期"><el-date-picker v-model="assignForm.startDate" type="date" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="assignDialogVisible = false">取消</el-button><el-button type="primary" :loading="assignLoading" @click="submitAssign">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete, Refresh, User } from '@element-plus/icons-vue'
import { getOrgTree, createOrgNode, updateOrgNode, deleteOrgNode, getUserList, getPositionList, getJobTitleList, getOrgUserPositions, assignUserPosition, removeUserPosition, setPrimaryPosition } from '@/api/system'

const treeRef = ref(null)
const loading = ref(false)
const treeData = ref([])

const treeProps = {
  children: 'children',
  label: 'name'
}

const levelName = (level) => {
  const map = { 1: '地区', 2: '县市', 3: '乡镇街道', 4: '单位', 5: '科室' }
  return map[level] || '未知'
}

const levelTagType = (level) => {
  const map = { 1: '', 2: 'success', 3: 'warning', 4: 'danger', 5: 'info' }
  return map[level] || 'info'
}

const orgTypeLabel = (t) => {
  const map = { ENTERPRISE: '企业', GOVERNMENT: '机关', INSTITUTION: '事业单位', SOCIAL_GROUP: '社会团体' }
  return map[t] || t
}

// 加载树
const loadTree = async () => {
  loading.value = true
  try {
    const res = await getOrgTree()
    treeData.value = res.data || []
  } catch (e) {
    ElMessage.error('加载组织架构失败')
  } finally {
    loading.value = false
  }
}

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const editId = ref(null)
const parentName = ref('')
const submitLoading = ref(false)
const formRef = ref(null)

const form = ref({
  name: '',
  parentId: null,
  level: 1,
  sortOrder: 0,
  status: 1,
  description: '',
  orgCode: '',
  orgType: '',
  unifiedSocialCreditCode: '',
  leaderName: '',
  leaderPhone: '',
  address: '',
  remark: ''
})

const formRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择层级', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const handleAdd = (parentNode) => {
  isEdit.value = false
  editId.value = null
  if (parentNode) {
    form.value = {
      name: '',
      parentId: parentNode.id,
      level: parentNode.level + 1,
      sortOrder: 0,
      status: 1,
      description: '',
      orgCode: '',
      orgType: '',
      unifiedSocialCreditCode: '',
      leaderName: '',
      leaderPhone: '',
      address: '',
      remark: ''
    }
    parentName.value = parentNode.name
    dialogTitle.value = `新增${levelName(parentNode.level + 1)}（上级：${parentNode.name}）`
  } else {
    form.value = {
      name: '',
      parentId: null,
      level: 1,
      sortOrder: 0,
      status: 1,
      description: '',
      orgCode: '',
      orgType: '',
      unifiedSocialCreditCode: '',
      leaderName: '',
      leaderPhone: '',
      address: '',
      remark: ''
    }
    parentName.value = ''
    dialogTitle.value = '新增地区'
  }
  dialogVisible.value = true
}

const handleEdit = (node) => {
  isEdit.value = true
  editId.value = node.id
  form.value = {
    name: node.name,
    parentId: node.parentId,
    level: node.level,
    sortOrder: node.sortOrder || 0,
    status: node.status,
    description: node.description || '',
    orgCode: node.orgCode || '',
    orgType: node.orgType || '',
    unifiedSocialCreditCode: node.unifiedSocialCreditCode || '',
    leaderName: node.leaderName || '',
    leaderPhone: node.leaderPhone || '',
    address: node.address || '',
    remark: node.remark || ''
  }
  parentName.value = node.parentId ? '（上级节点）' : ''
  dialogTitle.value = `编辑 - ${node.name}`
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateOrgNode(editId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createOrgNode(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadTree()
  } catch (e) {
    if (e.response?.data?.message) {
      ElMessage.error(e.response.data.message)
    } else {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    }
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (node) => {
  try {
    await deleteOrgNode(node.id)
    ElMessage.success('删除成功')
    loadTree()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

// 人员管理
const memberDialogVisible = ref(false)
const memberOrgId = ref(null)
const memberOrgName = ref('')
const memberList = ref([])

const handleViewMembers = async (node) => {
  memberOrgId.value = node.id
  memberOrgName.value = node.name
  memberDialogVisible.value = true
  try {
    const res = await getOrgUserPositions(node.id)
    memberList.value = res.data || []
  } catch (e) { memberList.value = [] }
}

const handleSetPrimary = async (row) => {
  try {
    await setPrimaryPosition(row.id)
    ElMessage.success('已设为主职')
    const res = await getOrgUserPositions(memberOrgId.value)
    memberList.value = res.data || []
  } catch (e) { ElMessage.error('设置失败') }
}

const handleRemoveMember = async (row) => {
  try {
    await removeUserPosition(row.id)
    ElMessage.success('已移除')
    const res = await getOrgUserPositions(memberOrgId.value)
    memberList.value = res.data || []
  } catch (e) { ElMessage.error('移除失败') }
}

// 分配人员
const assignDialogVisible = ref(false)
const assignLoading = ref(false)
const assignFormRef = ref(null)
const userOptions = ref([])
const positionOptions = ref([])
const jobTitleOptions = ref([])
const assignForm = ref({ userId: null, orgId: null, positionId: null, jobTitleId: null, isPrimary: 1, startDate: null })
const assignRules = { userId: [{ required: true, message: '请选择用户', trigger: 'change' }] }

const handleAssignMember = async () => {
  assignForm.value = { userId: null, orgId: memberOrgId.value, positionId: null, jobTitleId: null, isPrimary: 1, startDate: null }
  assignDialogVisible.value = true
  try {
    const [uRes, pRes, tRes] = await Promise.all([
      getUserList({ page: 0, size: 999 }),
      getPositionList({ page: 0, size: 999 }),
      getJobTitleList({ page: 0, size: 999 })
    ])
    userOptions.value = uRes.data?.content || uRes.data || []
    positionOptions.value = pRes.data?.content || pRes.data || []
    jobTitleOptions.value = tRes.data?.content || tRes.data || []
  } catch (e) { console.error(e) }
}

const submitAssign = async () => {
  const valid = await assignFormRef.value.validate().catch(() => false)
  if (!valid) return
  assignLoading.value = true
  try {
    await assignUserPosition(assignForm.value)
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    const res = await getOrgUserPositions(memberOrgId.value)
    memberList.value = res.data || []
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '分配失败')
  } finally { assignLoading.value = false }
}

onMounted(() => {
  loadTree()
})
</script>

<style lang="scss" scoped>
.org-container {
  padding: 20px;
}

.org-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  h3 {
    margin: 0;
    font-size: 18px;
    color: #303133;
  }

  .org-actions {
    display: flex;
    gap: 8px;
  }
}

.org-tree-wrapper {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #ebeef5;
  min-height: 400px;
}

.tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 4px 0;

  .node-info {
    display: flex;
    align-items: center;
    gap: 8px;

    .level-tag {
      min-width: 64px;
      text-align: center;
    }

    .node-name {
      font-size: 14px;
      color: #303133;
      font-weight: 500;
    }

    .status-tag {
      margin-left: 4px;
    }

    .leader-info {
      font-size: 12px;
      color: #909399;
    }
  }

  .node-actions {
    display: flex;
    align-items: center;
    gap: 4px;
    opacity: 0;
    transition: opacity 0.2s;
  }

  &:hover .node-actions {
    opacity: 1;
  }
}
</style>
