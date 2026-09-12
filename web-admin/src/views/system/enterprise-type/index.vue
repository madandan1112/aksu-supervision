<template>
  <div class="enterprise-type-page">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon size="20"><Collection /></el-icon>
          企业行业分类管理
        </h2>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>系统管理</el-breadcrumb-item>
          <el-breadcrumb-item>企业行业分类</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索分类名称"
          clearable
          prefix-icon="Search"
          style="width: 220px"
          @input="handleSearch"
        />
        <el-select
          v-model="filterCategory"
          placeholder="所属大类"
          clearable
          style="width: 160px"
          @change="handleCategoryFilter"
        >
          <el-option
            v-for="item in categoryOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增分类
        </el-button>
        <el-button @click="loadData">
          <el-icon><Refresh /></el-icon>刷新
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="page-content">
      <el-card shadow="never" class="table-card">
        <el-table
          :data="tableData"
          border
          stripe
          v-loading="loading"
          :header-cell-style="{ background: '#f5f7fa', fontWeight: 600 }"
        >
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="typeCode" label="分类编码" width="120" show-overflow-tooltip />
          <el-table-column prop="typeName" label="分类名称" width="150" show-overflow-tooltip />
          <el-table-column label="所属大类" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="categoryTagType(row.category)" size="small" effect="light">
                {{ categoryLabel(row.category) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="所需证照" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="license-tags">
                <el-tag
                  v-for="(license, index) in parseLicenses(row.requiredLicense)"
                  :key="index"
                  size="small"
                  type="warning"
                  effect="plain"
                  class="license-tag"
                >
                  {{ license }}
                </el-tag>
                <span v-if="!parseLicenses(row.requiredLicense).length" class="empty-text">无</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述说明" min-width="180" show-overflow-tooltip />
          <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
          <el-table-column label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small" effect="dark">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
              <el-popconfirm title="确定删除该分类吗？" @confirm="handleDelete(row)">
                <template #reference>
                  <el-button type="danger" link size="small">
                    <el-icon><Delete /></el-icon>删除
                  </el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="560px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="分类编码" prop="typeCode">
          <el-input v-model="form.typeCode" placeholder="请输入分类编码，如：A01" maxlength="20" />
        </el-form-item>
        <el-form-item label="分类名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="请输入分类名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="所属大类" prop="category">
          <el-select v-model="form.category" placeholder="请选择所属大类" style="width: 100%">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所需证照">
          <el-input
            v-model="licenseInput"
            type="textarea"
            :rows="3"
            placeholder="请输入所需证照，多个证照用逗号分隔，如：食品经营许可证,卫生许可证"
            maxlength="500"
          />
          <div class="form-tip">多个证照名称请用英文逗号（,）分隔</div>
        </el-form-item>
        <el-form-item label="描述说明">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="2"
            placeholder="请输入描述说明"
            maxlength="500"
          />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete, Refresh, Search, Collection } from '@element-plus/icons-vue'
import {
  getEnterpriseTypeList,
  createEnterpriseType,
  updateEnterpriseType,
  deleteEnterpriseType
} from '@/api/system'

// ==================== 分类选项 ====================
const categoryOptions = [
  { value: 'FOOD', label: '食品', tagType: 'danger' },
  { value: 'DRUG', label: '药品', tagType: 'warning' },
  { value: 'MEDICAL', label: '医疗器械', tagType: 'primary' },
  { value: 'SPECIAL_EQUIP', label: '特种设备', tagType: 'info' },
  { value: 'HOTEL', label: '酒店', tagType: 'success' },
  { value: 'FACTORY', label: '工厂', tagType: '' },
  { value: 'HAZARDOUS', label: '危化品', tagType: 'danger' },
  { value: 'COSMETICS', label: '化妆品', tagType: 'warning' },
  { value: 'TOBACCO', label: '烟草', tagType: 'primary' },
  { value: 'TRADE', label: '商贸', tagType: 'success' },
  { value: 'SERVICE', label: '服务', tagType: 'info' },
  { value: 'OTHER', label: '其他', tagType: '' }
]

// ==================== 数据状态 ====================
const loading = ref(false)
const tableData = ref([])
const searchKeyword = ref('')
const filterCategory = ref('')

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// ==================== 对话框状态 ====================
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)
const licenseInput = ref('')

const form = reactive({
  typeCode: '',
  typeName: '',
  category: '',
  requiredLicense: '',
  description: '',
  sortOrder: 0,
  status: 1
})

const formRules = {
  typeCode: [
    { required: true, message: '请输入分类编码', trigger: 'blur' },
    { max: 20, message: '编码长度不能超过20个字符', trigger: 'blur' }
  ],
  typeName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { max: 100, message: '名称长度不能超过100个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择所属大类', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// ==================== 工具函数 ====================
const categoryLabel = (value) => {
  const item = categoryOptions.find(c => c.value === value)
  return item ? item.label : value
}

const categoryTagType = (value) => {
  const item = categoryOptions.find(c => c.value === value)
  return item ? item.tagType : ''
}

const parseLicenses = (licenseStr) => {
  if (!licenseStr) return []
  try {
    const parsed = JSON.parse(licenseStr)
    if (Array.isArray(parsed)) return parsed
    return []
  } catch {
    // 如果不是JSON格式，尝试按逗号分隔
    return licenseStr.split(',').map(s => s.trim()).filter(Boolean)
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

// ==================== 数据加载 ====================
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    if (filterCategory.value) {
      params.category = filterCategory.value
    }
    const res = await getEnterpriseTypeList(params)
    const data = res.data || {}
    tableData.value = data.list || []
    pagination.total = data.total || 0
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// ==================== 搜索与筛选 ====================
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleCategoryFilter = () => {
  pagination.page = 1
  loadData()
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadData()
}

// ==================== 新增 ====================
const handleAdd = () => {
  isEdit.value = false
  editId.value = null
  dialogTitle.value = '新增企业行业分类'
  Object.assign(form, {
    typeCode: '',
    typeName: '',
    category: '',
    requiredLicense: '',
    description: '',
    sortOrder: 0,
    status: 1
  })
  licenseInput.value = ''
  dialogVisible.value = true
}

// ==================== 编辑 ====================
const handleEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  dialogTitle.value = '编辑企业行业分类'
  Object.assign(form, {
    typeCode: row.typeCode || '',
    typeName: row.typeName || '',
    category: row.category || '',
    requiredLicense: row.requiredLicense || '',
    description: row.description || '',
    sortOrder: row.sortOrder || 0,
    status: row.status ?? 1
  })
  // 将JSON格式的证照转换为逗号分隔文本
  const licenses = parseLicenses(row.requiredLicense)
  licenseInput.value = licenses.join(', ')
  dialogVisible.value = true
}

// ==================== 提交 ====================
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  // 将逗号分隔的证照转换为JSON数组字符串
  const licenses = licenseInput.value
    .split(/[,，]/)
    .map(s => s.trim())
    .filter(Boolean)
  form.requiredLicense = JSON.stringify(licenses)

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateEnterpriseType(editId.value, form)
      ElMessage.success('更新成功')
    } else {
      await createEnterpriseType(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await loadData()
  } catch (e) {
    const msg = e.response?.data?.message || (isEdit.value ? '更新失败' : '创建失败')
    ElMessage.error(msg)
  } finally {
    submitLoading.value = false
  }
}

// ==================== 删除 ====================
const handleDelete = async (row) => {
  try {
    await deleteEnterpriseType(row.id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (e) {
    const msg = e.response?.data?.message || '删除失败'
    ElMessage.error(msg)
  }
}

// ==================== 初始化 ====================
onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.enterprise-type-page {
  background: #f5f7fa;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.page-header {
  background: #fff;
  padding: 16px 24px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-left {
    .page-title {
      margin: 0 0 8px 0;
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}

.page-content {
  flex: 1;
  padding: 16px 24px;
  overflow: auto;
}

.table-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;

  :deep(.el-card__body) {
    padding: 16px;
  }
}

.license-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  align-items: center;

  .license-tag {
    margin: 0;
  }

  .empty-text {
    color: #909399;
    font-size: 12px;
  }
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}
</style>
