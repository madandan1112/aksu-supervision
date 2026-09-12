<template>
  <div class="enterprise-user-page">
    <!-- 顶部筛选栏 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon size="20"><UserFilled /></el-icon>
          企业用户管理
        </h2>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>用户管理</el-breadcrumb-item>
          <el-breadcrumb-item>企业用户</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon>新增用户
        </el-button>
      </div>
    </div>

    <!-- 筛选区 -->
    <div class="filter-section">
      <el-card shadow="never" class="filter-card">
        <el-form :model="queryParams" inline>
          <el-form-item label="关键词">
            <el-input v-model="queryParams.keyword" placeholder="用户名/姓名/手机号" clearable style="width: 180px" @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="县市">
            <el-select v-model="queryParams.area" placeholder="全部县市" clearable style="width: 150px" @change="handleSearch">
              <el-option v-for="a in areaOptions" :key="a" :label="a" :value="a" />
            </el-select>
          </el-form-item>
          <el-form-item label="行业分类">
            <el-select v-model="queryParams.industry" placeholder="全部行业" clearable style="width: 150px" @change="handleSearch">
              <el-option v-for="item in industryOptions" :key="item.typeCode" :label="item.typeName" :value="item.typeName" />
            </el-select>
          </el-form-item>
          <el-form-item label="权限角色">
            <el-select v-model="queryParams.roleId" placeholder="全部权限" clearable style="width: 150px" @change="handleSearch">
              <el-option label="企业负责人" :value="roleAdminId" />
              <el-option label="企业一般人员" :value="roleStaffId" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 100px" @change="handleSearch">
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" v-for="stat in statCards" :key="stat.label" :class="stat.cls">
        <div class="stat-value">{{ stat.value }}</div>
        <div class="stat-label">{{ stat.label }}</div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="page-content">
      <el-card shadow="never" class="table-card">
        <el-table :data="tableData" v-loading="loading" stripe border
          :header-cell-style="{ background: '#f5f7fa', fontWeight: 600 }">
          <el-table-column type="index" label="序号" width="55" align="center" />
          <el-table-column prop="username" label="用户名" width="120" show-overflow-tooltip />
          <el-table-column prop="realName" label="姓名" width="90">
            <template #default="{ row }">{{ row.realName || '-' }}</template>
          </el-table-column>
          <el-table-column prop="phone" label="手机号" width="130">
            <template #default="{ row }">{{ row.phone || '-' }}</template>
          </el-table-column>
          <el-table-column label="企业名称" min-width="160" show-overflow-tooltip>
            <template #default="{ row }">
              <span v-if="row.enterpriseName" class="link-text" @click="viewEnterprise(row)">{{ row.enterpriseName }}</span>
              <span v-else class="empty-text">未关联企业</span>
            </template>
          </el-table-column>
          <el-table-column label="权限角色" width="140" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.roleId === roleAdminId" type="primary" size="small" effect="dark">企业负责人</el-tag>
              <el-tag v-else-if="row.roleId === roleStaffId" type="success" size="small" effect="dark">企业一般人员</el-tag>
              <el-tag v-else type="info" size="small">未分配</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="所属县市" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.area" size="small" effect="plain">{{ row.area }}</el-tag>
              <span v-else class="empty-text">-</span>
            </template>
          </el-table-column>
          <el-table-column label="行业分类" width="110" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.industry" size="small" :type="industryTagType(row.industry)" effect="light">{{ row.industry }}</el-tag>
              <span v-else class="empty-text">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="70" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small" effect="dark">
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="260" fixed="right" align="center">
            <template #default="{ row }">
              <el-button text type="primary" size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
              <el-button text type="warning" size="small" @click="openPermDialog(row)">
                <el-icon><Lock /></el-icon>权限
              </el-button>
              <el-button text type="info" size="small" @click="handleResetPwd(row)">重置密码</el-button>
              <el-popconfirm title="确定删除该用户吗？" @confirm="handleDelete(row)">
                <template #reference>
                  <el-button text type="danger" size="small">
                    <el-icon><Delete /></el-icon>删除
                  </el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchData"
            @current-change="fetchData"
          />
        </div>
      </el-card>
    </div>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑企业用户' : '新增企业用户'" width="520px" destroy-on-close :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名（实名）" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入18位身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="照片上传">
          <div class="user-photo-box">
            <el-image v-if="form.photoUrl" :src="form.photoUrl" fit="cover" class="user-photo-img" />
            <div v-else class="user-photo-empty">未上传</div>
            <div class="user-photo-actions">
              <el-button size="small" type="primary" @click="triggerFileUpload('photo')">更换</el-button>
              <el-button v-if="form.photoUrl" size="small" type="danger" @click="form.photoUrl = ''">删除</el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="个人资质">
          <div class="user-photo-box">
            <el-image v-if="form.qualificationUrl" :src="form.qualificationUrl" fit="cover" class="user-photo-img" />
            <div v-else class="user-photo-empty">未上传（可选）</div>
            <div class="user-photo-actions">
              <el-button size="small" type="primary" @click="triggerFileUpload('qualification')">更换</el-button>
              <el-button v-if="form.qualificationUrl" size="small" type="danger" @click="form.qualificationUrl = ''">删除</el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="权限角色" prop="roleId">
          <el-select v-model="form.roleId" style="width:100%" placeholder="请选择权限角色">
            <el-option label="企业负责人" :value="roleAdminId" />
            <el-option label="企业一般人员" :value="roleStaffId" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="isEdit">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog v-model="permDialogVisible" title="权限分配" width="520px" destroy-on-close :close-on-click-modal="false">
      <div class="perm-user-info">
        <span>用户：<b>{{ permUser.realName || permUser.username }}</b></span>
        <span>企业：{{ permUser.enterpriseName || '未关联' }}</span>
      </div>
      <el-divider />
      <el-form label-width="100px">
        <el-form-item label="权限角色" class="role-select-item">
          <div class="role-options">
            <div
              class="role-card"
              :class="{ active: permForm.roleId === roleAdminId }"
              @click="permForm.roleId = roleAdminId"
            >
              <div class="role-card-header">
                <el-radio :value="roleAdminId" :label="roleAdminId">企业负责人</el-radio>
              </div>
              <div class="role-card-desc">
                可管理企业信息、收到执法下发的文件、处理文件、执行整改
              </div>
              <div class="role-card-perms">
                <el-tag size="small" type="primary" effect="plain">企业信息管理</el-tag>
                <el-tag size="small" type="success" effect="plain">执法文件接收</el-tag>
                <el-tag size="small" type="warning" effect="plain">整改处理</el-tag>
              </div>
            </div>
            <div
              class="role-card"
              :class="{ active: permForm.roleId === roleStaffId }"
              @click="permForm.roleId = roleStaffId"
            >
              <div class="role-card-header">
                <el-radio :value="roleStaffId" :label="roleStaffId">企业一般人员</el-radio>
              </div>
              <div class="role-card-desc">
                负责文字类上传、企业信息检查、整改类型上传，不可修改企业信息及负责人权限
              </div>
              <div class="role-card-perms">
                <el-tag size="small" type="info" effect="plain">文字类上传</el-tag>
                <el-tag size="small" type="info" effect="plain">检查整改上传</el-tag>
                <el-tag size="small" type="danger" effect="plain">不可修改企业信息</el-tag>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="数据权限">
          <el-tag type="info" size="small">仅查看本企业数据（SELF）</el-tag>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permLoading" @click="submitPerm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 企业详情侧边抽屉 -->
    <el-drawer v-model="drawerVisible" title="企业详细信息" size="600px" destroy-on-close>
      <div v-if="currentEnterprise" class="enterprise-detail">
        <div class="detail-header">
          <div class="detail-name">{{ currentEnterprise.name || currentEnterprise.enterpriseName }}</div>
          <el-tag v-if="currentEnterprise.status === 1" type="success" effect="dark">正常</el-tag>
          <el-tag v-else type="danger" effect="dark">禁用</el-tag>
        </div>
        <el-descriptions :column="2" border size="default">
          <el-descriptions-item label="统一社会信用代码" :span="2">{{ currentEnterprise.creditCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="法定代表人">{{ currentEnterprise.legalPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentEnterprise.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属县市">
            <el-tag v-if="currentEnterprise.area" size="small" effect="plain">{{ currentEnterprise.area }}</el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="行业分类">
            <el-tag v-if="currentEnterprise.industry" size="small" type="warning" effect="light">{{ currentEnterprise.industry }}</el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentEnterprise.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="详细地址" :span="2">{{ currentEnterprise.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="经营范围" :span="2">{{ currentEnterprise.businessScope || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">{{ currentEnterprise.registrationStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(currentEnterprise.createTime) }}</el-descriptions-item>
        </el-descriptions>

        <!-- 证照图片 -->
        <div class="detail-section">
          <h4>证照信息</h4>
          <div class="photo-grid">
            <div class="photo-card">
              <div class="photo-label">营业执照</div>
              <div class="photo-content">
                <el-image v-if="currentEnterprise.licenseUrl" :src="currentEnterprise.licenseUrl" :preview-src-list="[currentEnterprise.licenseUrl]" fit="contain" class="photo-img" />
                <div v-else class="photo-empty">未上传</div>
              </div>
            </div>
            <div class="photo-card">
              <div class="photo-label">门头照</div>
              <div class="photo-content">
                <el-image v-if="currentEnterprise.storefrontPhoto" :src="currentEnterprise.storefrontPhoto" :preview-src-list="[currentEnterprise.storefrontPhoto]" fit="cover" class="photo-img" />
                <div v-else class="photo-empty">未上传</div>
              </div>
            </div>
            <div class="photo-card">
              <div class="photo-label">店内照</div>
              <div class="photo-content">
                <el-image v-if="currentEnterprise.interiorPhoto" :src="currentEnterprise.interiorPhoto" :preview-src-list="[currentEnterprise.interiorPhoto]" fit="cover" class="photo-img" />
                <div v-else class="photo-empty">未上传</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 资质证书 -->
        <div v-if="drawerQualificationList.length" class="detail-section">
          <h4>资质证书（{{ drawerQualificationList.length }}张）</h4>
          <div class="cert-grid">
            <div v-for="(url, idx) in drawerQualificationList" :key="idx" class="cert-card">
              <el-image :src="url" :preview-src-list="drawerQualificationList" :initial-index="idx" fit="contain" class="cert-img" />
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="该用户未关联企业" />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, Search, Refresh, Plus, Edit, Lock, Delete } from '@element-plus/icons-vue'
import {
  getMiniappUserList,
  createMiniappUser,
  getMiniappUserCount,
  resetUserPassword,
  deleteUser,
  updateUser,
  assignUserRole,
  getActiveEnterpriseTypes,
  getEnterpriseDetailById,
  getAllRolesWithDataPermission
} from '@/api/system'
import request from '@/utils/request'

// ==================== 企业角色ID（启动时按角色编码解析，接口失败回退12/13） ====================
const roleAdminId = ref(12)
const roleStaffId = ref(13)
const resolveEnterpriseRoleIds = async () => {
  try {
    const res = await getAllRolesWithDataPermission()
    const roles = res.data || []
    const admin = roles.find(r => r.roleCode === 'ENTERPRISE_ADMIN')
    const staff = roles.find(r => r.roleCode === 'ENTERPRISE_ENFORCER' || r.roleCode === 'ENTERPRISE_USER')
    if (admin) roleAdminId.value = admin.id
    if (staff) roleStaffId.value = staff.id
  } catch { /* 保留回退值 */ }
}

// ==================== 常量 ====================
const areaOptions = [
  '阿克苏市', '库车市', '温宿县', '拜城县', '新和县',
  '沙雅县', '乌什县', '阿瓦提县', '柯坪县'
]

// ==================== 数据 ====================
const loading = ref(false)
const total = ref(0)
const totalCount = ref(0)
const dialogVisible = ref(false)
const permDialogVisible = ref(false)
const drawerVisible = ref(false)
const submitLoading = ref(false)
const permLoading = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const industryOptions = ref([])
const tableData = ref([])
const currentEnterprise = ref(null)
const permUser = ref({})

// 企业详情抽屉中资质证书列表
const drawerQualificationList = computed(() => {
  if (!currentEnterprise.value?.qualificationUrls) return []
  try {
    const parsed = typeof currentEnterprise.value.qualificationUrls === 'string'
      ? JSON.parse(currentEnterprise.value.qualificationUrls) : currentEnterprise.value.qualificationUrls
    return Array.isArray(parsed) ? parsed : []
  } catch { return [] }
})

const queryParams = reactive({ keyword: '', area: '', industry: '', roleId: null, status: null, page: 1, size: 10 })
const form = reactive({ username: '', password: '', realName: '', idCard: '', phone: '', photoUrl: '', qualificationUrl: '', roleId: null, status: 1 })
const permForm = reactive({ roleId: null })

// 文件上传
const fileInputRef = ref(null)
const currentUploadType = ref('')

const triggerFileUpload = (type) => {
  currentUploadType.value = type
  fileInputRef.value?.click()
}

const handleFileUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await request.post('/file/upload', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
    const url = res.data?.url || res.data?.data?.url || ''
    if (!url) { ElMessage.error('上传失败'); return }
    if (currentUploadType.value === 'photo') form.photoUrl = url
    if (currentUploadType.value === 'qualification') form.qualificationUrl = url
    ElMessage.success('上传成功')
  } catch { ElMessage.error('上传失败') }
  e.target.value = ''
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码不少于6位', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择权限角色', trigger: 'change' }]
}

// ==================== 统计卡片 ====================
const statCards = computed(() => [
  { label: '企业用户总数', value: totalCount.value, cls: 'stat-blue' },
  { label: '当前筛选结果', value: total.value, cls: 'stat-green' },
])

// ==================== 工具函数 ====================
const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const industryTagType = (name) => {
  const map = {
    '食品': 'danger', '药品': 'warning', '医疗': 'primary',
    '特设': 'info', '酒店': 'success', '危化': 'danger',
    '化妆品': 'warning', '烟草': 'primary', '商贸': 'success'
  }
  for (const [key, val] of Object.entries(map)) {
    if (name && name.includes(key)) return val
  }
  return ''
}

// ==================== 数据加载 ====================
const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.keyword) params.keyword = queryParams.keyword
    if (queryParams.area) params.area = queryParams.area
    if (queryParams.industry) params.industry = queryParams.industry
    if (queryParams.roleId) params.roleId = queryParams.roleId
    if (queryParams.status !== null && queryParams.status !== '') params.status = queryParams.status
    const res = await getMiniappUserList(params)
    const list = res.data?.list || []
    tableData.value = list
    total.value = res.data?.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const fetchCount = async () => {
  try {
    const res = await getMiniappUserCount()
    totalCount.value = res.data?.total || 0
  } catch { /* ignore */ }
}

const fetchIndustryOptions = async () => {
  try {
    const res = await getActiveEnterpriseTypes()
    industryOptions.value = res.data || []
  } catch { /* ignore */ }
}

// ==================== 操作 ====================
const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => {
  queryParams.keyword = ''; queryParams.area = ''; queryParams.industry = ''; queryParams.roleId = null; queryParams.status = null
  handleSearch()
}

const viewEnterprise = async (row) => {
  if (!row.enterpriseId) {
    ElMessage.info('该用户未关联企业')
    return
  }
  try {
    const res = await getEnterpriseDetailById(row.enterpriseId)
    currentEnterprise.value = res.data?.enterprise || res.data || null
    drawerVisible.value = true
  } catch {
    ElMessage.error('获取企业详情失败')
  }
}

// ========== 新增/编辑用户 ==========
const openDialog = (row) => {
    if (row) {
    isEdit.value = true
    editId.value = row.id
    form.username = row.username || ''
    form.realName = row.realName || ''
    form.idCard = row.idCard || ''
    form.phone = row.phone || ''
    form.photoUrl = row.photoUrl || ''
    form.qualificationUrl = row.qualificationUrl || ''
    form.roleId = row.roleId || roleAdminId.value
    form.status = row.status ?? 1
  } else {
    isEdit.value = false
    editId.value = null
    form.username = ''; form.password = ''; form.realName = ''; form.idCard = ''; form.phone = ''; form.photoUrl = ''; form.qualificationUrl = ''; form.roleId = roleAdminId.value; form.status = 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  openDialog(row)
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      // 编辑：更新基本信息 + 角色分配
      await updateUser(editId.value, {
        realName: form.realName,
        idCard: form.idCard,
        phone: form.phone,
        photoUrl: form.photoUrl,
        qualificationUrl: form.qualificationUrl,
        status: form.status
      })
      // 分配角色
      if (form.roleId) {
        await assignUserRole(editId.value, form.roleId)
      }
      ElMessage.success('更新成功')
    } else {
      // 新增：创建用户 + 角色分配
      await createMiniappUser({
        username: form.username,
        password: form.password,
        realName: form.realName,
        phone: form.phone,
        userType: 'enterprise_user',
        roleId: form.roleId
      })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
    fetchCount()
  } catch (e) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally { submitLoading.value = false }
}

// ========== 权限分配 ==========
const openPermDialog = (row) => {
  permUser.value = row
  permForm.roleId = row.roleId || roleAdminId.value
  permDialogVisible.value = true
}

const submitPerm = async () => {
  permLoading.value = true
  try {
    await assignUserRole(permUser.value.id, permForm.roleId)
    ElMessage.success('权限分配成功')
    permDialogVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('权限分配失败')
  } finally { permLoading.value = false }
}

// ========== 其他操作 ==========
const handleResetPwd = async (row) => {
  try {
    await ElMessageBox.confirm(`确定重置用户 ${row.username} 的密码为 123456？`, '重置密码', { type: 'warning' })
    await resetUserPassword(row.id)
    ElMessage.success('密码已重置为 123456')
  } catch { /* cancelled */ }
}

const handleDelete = async (row) => {
  try {
    await deleteUser(row.id)
    ElMessage.success('已删除')
    fetchData()
    fetchCount()
  } catch { ElMessage.error('删除失败') }
}

onMounted(() => { fetchData(); fetchCount(); fetchIndustryOptions(); resolveEnterpriseRoleIds() })
</script>

<style lang="scss" scoped>
.enterprise-user-page {
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
}

.filter-section {
  padding: 12px 24px 0;

  .filter-card {
    border-radius: 8px;
    border: 1px solid #ebeef5;
  }
}

.stats-row {
  display: flex;
  gap: 16px;
  padding: 12px 24px;

  .stat-card {
    flex: 1;
    background: #fff;
    border-radius: 8px;
    padding: 16px 20px;
    border: 1px solid #ebeef5;
    text-align: center;

    .stat-value {
      font-size: 28px;
      font-weight: 700;
      line-height: 1.2;
    }

    .stat-label {
      font-size: 13px;
      color: #909399;
      margin-top: 4px;
    }

    &.stat-blue .stat-value { color: #409eff; }
    &.stat-green .stat-value { color: #67c23a; }
  }
}

.page-content {
  flex: 1;
  padding: 0 24px 24px;
}

.table-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;

  :deep(.el-card__body) { padding: 16px; }
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.link-text {
  color: #409eff;
  cursor: pointer;
  &:hover { text-decoration: underline; }
}

.empty-text {
  color: #c0c4cc;
  font-size: 12px;
}

// 权限分配对话框
.perm-user-info {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #606266;

  b { color: #303133; }
}

.role-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
  line-height: 1.4;
}

.role-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.role-card {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #409eff;
  }

  &.active {
    border-color: #409eff;
    background: #ecf5ff;
  }

  .role-card-header {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
  }

  .role-card-desc {
    font-size: 13px;
    color: #606266;
    margin-bottom: 10px;
    line-height: 1.5;
  }

  .role-card-perms {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
  }
}

// 企业详情抽屉
.enterprise-detail {
  .detail-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;

    .detail-name {
      font-size: 20px;
      font-weight: 700;
      color: #303133;
    }
  }

  .detail-section {
    margin-top: 20px;

    h4 {
      margin: 0 0 12px 0;
      font-size: 15px;
      color: #303133;
      padding-bottom: 8px;
      border-bottom: 1px solid #ebeef5;
    }
  }
}

.photo-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.photo-card {
  .photo-label { font-size: 13px; color: #909399; margin-bottom: 6px; text-align: center; }
  .photo-content {
    border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden; height: 140px;
    display: flex; align-items: center; justify-content: center; background: #fafafa;
  }
  .photo-img { width: 100%; height: 100%; }
  .photo-empty { font-size: 13px; color: #c0c4cc; }
}
.cert-grid { display: flex; flex-wrap: wrap; gap: 8px; }
.cert-card {
  width: 120px; height: 100px; border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden;
}
.cert-img { width: 100%; height: 100%; }
</style>
