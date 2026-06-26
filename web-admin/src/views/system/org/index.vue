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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import { getOrgTree, createOrgNode, updateOrgNode, deleteOrgNode } from '@/api/system'

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
  description: ''
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
      description: ''
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
      description: ''
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
    description: node.description || ''
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
