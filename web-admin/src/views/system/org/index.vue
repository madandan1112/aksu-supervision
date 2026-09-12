<template>
  <div class="org-page">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon size="20"><OfficeBuilding /></el-icon>
          组织架构管理
        </h2>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>系统管理</el-breadcrumb-item>
          <el-breadcrumb-item>组织架构</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索组织名称"
          clearable
          prefix-icon="Search"
          style="width: 200px"
          @input="handleSearch"
        />
        <el-button type="primary" @click="handleAdd(null)">
          <el-icon><Plus /></el-icon>新增地区
        </el-button>
        <el-button @click="loadTree">
          <el-icon><Refresh /></el-icon>刷新
        </el-button>
      </div>
    </div>

    <!-- 主体左右分栏 -->
    <div class="org-body" v-loading="loading">
      <!-- 左侧：组织架构树 -->
      <div class="org-sidebar">
        <div class="sidebar-header">
          <span class="sidebar-title">组织架构树</span>
          <el-tag size="small" type="info">共 {{ totalNodes }} 个节点</el-tag>
        </div>
        
        <div class="tree-container">
          <el-tree
            ref="treeRef"
            :data="filteredTreeData"
            :props="treeProps"
            node-key="id"
            :expand-on-click-node="false"
            highlight-current
            :current-node-key="selectedNode?.id"
            draggable
            :allow-drop="allowDrop"
            :allow-drag="allowDrag"
            @node-drop="handleNodeDrop"
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <div class="custom-tree-node" :class="{ 'is-active': selectedNode?.id === data.id }">
                <div class="node-main">
                  <!-- 层级指示器 -->
                  <div class="level-indicator" :class="data.level === 2 ? (data.regionSubType === 'CITY' ? 'level-2' : 'level-2-county') : `level-${data.level}`">
                    <el-icon size="14">
                      <MapLocation v-if="data.level === 1" />
                      <Location v-else-if="data.level === 2" />
                      <OfficeBuilding v-else-if="data.level === 3" />
                      <School v-else-if="data.level === 4" />
                      <User v-else />
                    </el-icon>
                  </div>
                  
                  <!-- 节点内容 -->
                  <div class="node-content">
                    <div class="node-title-row">
                      <span class="node-name" :title="data.name">{{ data.name }}</span>
                      <el-tag 
                        :type="levelTagType(data.level, data.regionSubType)" 
                        size="small" 
                        effect="light"
                        class="level-tag"
                      >
                        {{ levelName(data.level, data.regionSubType) }}
                      </el-tag>
                    </div>
                    <div class="node-meta" v-if="data.orgCode || data.leaderName">
                      <span v-if="data.orgCode" class="meta-item">
                        <el-icon size="10"><CollectionTag /></el-icon>
                        {{ data.orgCode }}
                      </span>
                      <span v-if="data.leaderName" class="meta-item">
                        <el-icon size="10"><UserFilled /></el-icon>
                        {{ data.leaderName }}
                      </span>
                    </div>
                  </div>
                </div>
                
                <!-- 悬停操作 -->
                <div class="node-actions" @click.stop>
                  <el-dropdown trigger="click" size="small">
                    <el-button link type="primary" size="small">
                      <el-icon><MoreFilled /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item v-if="data.level < 5" @click="handleAdd(data)">
                          <el-icon><Plus /></el-icon>新增下级
                        </el-dropdown-item>
                        <el-dropdown-item @click="handleEdit(data)">
                          <el-icon><Edit /></el-icon>编辑
                        </el-dropdown-item>
                        <el-dropdown-item @click="handleViewMembers(data)">
                          <el-icon><User /></el-icon>管理人员
                        </el-dropdown-item>
                        <el-dropdown-item divided @click="handleDelete(data)" style="color: #f56c6c;">
                          <el-icon><Delete /></el-icon>删除
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
            </template>
          </el-tree>
        </div>
        
        <!-- 层级图例 -->
        <div class="sidebar-footer">
          <div class="level-legend">
            <div class="legend-item"><span class="legend-dot level-1"></span>地区局</div>
            <div class="legend-item"><span class="legend-dot level-2"></span>市局/县局</div>
            <div class="legend-item"><span class="legend-dot level-3"></span>分局</div>
            <div class="legend-item"><span class="legend-dot level-4"></span>科室</div>
          </div>
        </div>
      </div>

      <!-- 右侧：详情面板 -->
      <div class="org-detail-panel" v-if="selectedNode">
        <!-- 组织名片头部 -->
        <div class="detail-header">
          <div class="detail-avatar" :class="selectedNode.level === 2 ? (selectedNode.regionSubType === 'CITY' ? 'level-2' : 'level-2-county') : `level-${selectedNode.level}`">
            <el-icon size="32">
              <MapLocation v-if="selectedNode.level === 1" />
              <Location v-else-if="selectedNode.level === 2" />
              <OfficeBuilding v-else-if="selectedNode.level === 3" />
              <School v-else-if="selectedNode.level === 4" />
              <User v-else />
            </el-icon>
          </div>
          <div class="detail-title-group">
            <h3 class="detail-name">{{ selectedNode.name }}</h3>
            <div class="detail-tags">
              <el-tag :type="levelTagType(selectedNode.level, selectedNode.regionSubType)" effect="dark" size="small">
                {{ levelName(selectedNode.level, selectedNode.regionSubType) }}
              </el-tag>
              <el-tag v-if="selectedNode.status === 0" type="danger" effect="dark" size="small">已禁用</el-tag>
              <el-tag v-else type="success" effect="dark" size="small">正常</el-tag>
              <el-tag v-if="selectedNode.orgType" type="warning" effect="plain" size="small">
                {{ orgTypeLabel(selectedNode.orgType) }}
              </el-tag>
            </div>
          </div>
          <div class="detail-actions">
            <el-button type="primary" @click="handleEdit(selectedNode)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button v-if="selectedNode.level < 5" @click="handleAdd(selectedNode)">
              <el-icon><Plus /></el-icon>新增下级
            </el-button>
          </div>
        </div>

        <!-- 信息卡片网格 -->
        <div class="detail-cards">
          <!-- 基本信息 -->
          <el-card class="info-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Document /></el-icon>
                <span>基本信息</span>
              </div>
            </template>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="组织编码">{{ selectedNode.orgCode || '-' }}</el-descriptions-item>
              <el-descriptions-item label="组织类型">{{ orgTypeLabel(selectedNode.orgType) || '-' }}</el-descriptions-item>
              <el-descriptions-item label="信用代码" :span="2">{{ selectedNode.unifiedSocialCreditCode || '-' }}</el-descriptions-item>
              <el-descriptions-item label="排序号">{{ selectedNode.sortOrder || 0 }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="selectedNode.status === 1 ? 'success' : 'danger'" size="small">
                  {{ selectedNode.status === 1 ? '正常' : '已禁用' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间" :span="2">{{ formatTime(selectedNode.createTime) }}</el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- 负责人信息 -->
          <el-card class="info-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><UserFilled /></el-icon>
                <span>负责人信息</span>
              </div>
            </template>
            <div v-if="selectedNode.leaderName" class="leader-profile">
              <div class="leader-avatar">
                <el-avatar :size="48" :icon="UserFilled" />
              </div>
              <div class="leader-info">
                <div class="leader-name">{{ selectedNode.leaderName }}</div>
                <div class="leader-phone" v-if="selectedNode.leaderPhone">
                  <el-icon><Phone /></el-icon>
                  {{ selectedNode.leaderPhone }}
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无负责人信息" :image-size="60" />
          </el-card>

          <!-- 地址信息 -->
          <el-card class="info-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Location /></el-icon>
                <span>联系信息</span>
              </div>
            </template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="地址">{{ selectedNode.address || '-' }}</el-descriptions-item>
              <el-descriptions-item label="备注">{{ selectedNode.remark || '-' }}</el-descriptions-item>
              <el-descriptions-item label="描述">{{ selectedNode.description || '-' }}</el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- 人员统计 -->
          <el-card class="info-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><User /></el-icon>
                <span>人员统计</span>
                <el-button type="primary" link size="small" @click="handleViewMembers(selectedNode)">
                  查看全部
                </el-button>
              </div>
            </template>
            <div class="stats-row">
              <div class="stat-item">
                <div class="stat-value">{{ memberCount }}</div>
                <div class="stat-label">总人数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ primaryCount }}</div>
                <div class="stat-label">主职人数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ childCount }}</div>
                <div class="stat-label">下级组织</div>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 未选中占位 -->
      <div class="org-detail-empty" v-else>
        <el-empty description="请点击左侧组织节点查看详情">
          <template #image>
            <el-icon size="80" color="#dcdfe6"><OfficeBuilding /></el-icon>
          </template>
        </el-empty>
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="所属上级" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="orgTreeOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            placeholder="不选则为顶级节点"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="层级" prop="level">
          <el-select v-model="form.level" placeholder="请选择层级" style="width: 100%">
            <el-option label="地区局（第1级）" :value="1" />
            <el-option label="市局/县局（第2级）" :value="2" />
            <el-option label="分局（第3级）" :value="3" />
            <el-option label="科室（第4级）" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="行政区划" v-if="form.level === 2">
          <el-select v-model="form.regionSubType" placeholder="请选择县级市/县" style="width: 100%">
            <el-option label="县级市" value="CITY" />
            <el-option label="县" value="COUNTY" />
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
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="1000" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 人员管理对话框 -->
    <el-dialog v-model="memberDialogVisible" width="900px" destroy-on-close>
      <template #header>
        <div class="member-dialog-header">
          <span>{{ memberOrgName }} - 人员列表</span>
          <el-button type="primary" size="small" @click="handleAssignMember">
            <el-icon><Plus /></el-icon>分配人员
          </el-button>
        </div>
      </template>
      <el-table :data="memberList" border stripe size="small">
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="positionName" label="岗位" width="120" />
        <el-table-column prop="jobTitleName" label="职务" width="120" />
        <el-table-column label="主职" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isPrimary === 1 ? '' : 'info'" size="small">
              {{ row.isPrimary === 1 ? '主职' : '兼职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="任职日期" width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.isPrimary !== 1" type="primary" link size="small" @click="handleSetPrimary(row)">
              设为主职
            </el-button>
            <el-popconfirm title="确定移除？" @confirm="handleRemoveMember(row)">
              <template #reference>
                <el-button type="danger" link size="small">移除</el-button>
              </template>
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
        <el-form-item label="是否主职">
          <el-radio-group v-model="assignForm.isPrimary">
            <el-radio :value="1">主职</el-radio>
            <el-radio :value="0">兼职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="任职日期">
          <el-date-picker v-model="assignForm.startDate" type="date" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignLoading" @click="submitAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Edit, Delete, Refresh, User, UserFilled,
  OfficeBuilding, MapLocation, Location, School,
  Document, Phone, MoreFilled, CollectionTag, Search
} from '@element-plus/icons-vue'
import {
  getOrgTree, createOrgNode, updateOrgNode, deleteOrgNode,
  getUserList, getPositionList, getJobTitleList,
  getOrgUserPositions, assignUserPosition, removeUserPosition, setPrimaryPosition
} from '@/api/system'

const treeRef = ref(null)
const loading = ref(false)
const treeData = ref([])
const selectedNode = ref(null)
const searchKeyword = ref('')

// 过滤后的树数据
const filteredTreeData = computed(() => {
  if (!searchKeyword.value) return treeData.value
  const keyword = searchKeyword.value.toLowerCase()
  const filter = (nodes) => {
    return nodes.filter(node => {
      const match = node.name?.toLowerCase().includes(keyword) ||
        node.orgCode?.toLowerCase().includes(keyword) ||
        node.leaderName?.toLowerCase().includes(keyword)
      if (node.children) {
        node.children = filter(node.children)
        return match || node.children.length > 0
      }
      return match
    })
  }
  return filter(JSON.parse(JSON.stringify(treeData.value)))
})

// 统计节点数
const totalNodes = computed(() => {
  let count = 0
  const traverse = (nodes) => {
    nodes.forEach(node => {
      count++
      if (node.children) traverse(node.children)
    })
  }
  if (treeData.value) traverse(treeData.value)
  return count
})

// 选中节点的统计
const memberCount = computed(() => memberList.value.length)
const primaryCount = computed(() => memberList.value.filter(m => m.isPrimary === 1).length)
const childCount = computed(() => selectedNode.value?.children?.length || 0)

const treeProps = {
  children: 'children',
  label: 'name'
}

 const levelName = (level, regionSubType) => {
  if (level === 1) return '地区局'
  if (level === 2) {
    return regionSubType === 'CITY' ? '市局' : '县局'
  }
  const map = { 3: '分局', 4: '科室' }
  return map[level] || '未知'
 }

 const levelTagType = (level, regionSubType) => {
  if (level === 1) return 'danger'
  if (level === 2) {
    return regionSubType === 'CITY' ? '' : 'warning'
  }
  const map = { 3: 'primary', 4: 'info' }
  return map[level] || 'info'
 }

const orgTypeLabel = (t) => {
  const map = { ENTERPRISE: '企业', GOVERNMENT: '机关', INSTITUTION: '事业单位', SOCIAL_GROUP: '社会团体' }
  return map[t] || t
}

const formatTime = (t) => {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 19)
}

// 加载树
const loadTree = async () => {
  loading.value = true
  try {
    const res = await getOrgTree()
    treeData.value = res.data || []
    // 如果之前有选中节点，尝试重新选中
    if (selectedNode.value) {
      const findNode = (nodes, id) => {
        for (const node of nodes) {
          if (node.id === id) return node
          if (node.children) {
            const found = findNode(node.children, id)
            if (found) return found
          }
        }
        return null
      }
      const refreshed = findNode(treeData.value, selectedNode.value.id)
      if (refreshed) {
        selectedNode.value = refreshed
        // 重新加载人员
        loadMembers(refreshed.id)
      }
    }
  } catch (e) {
    ElMessage.error('加载组织架构失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  // 搜索会自动触发 computed 过滤
}

const handleNodeClick = (data) => {
  selectedNode.value = data
  loadMembers(data.id)
}

// 拖拽排序
const allowDrag = (draggingNode) => {
  return true
}

const allowDrop = (draggingNode, dropNode, type) => {
  if (draggingNode.data.id === dropNode.data.id) return false
  // inner: 作为子节点，要求目标层级小于拖拽节点层级
  if (type === 'inner') {
    return dropNode.data.level < draggingNode.data.level
  }
  // before/after: 允许移动到同级
  return true
}

const handleNodeDrop = async (draggingNode, dropNode, dropType) => {
  const draggingId = draggingNode.data.id
  let newParentId = draggingNode.data.parentId

  if (dropType === 'inner') {
    newParentId = dropNode.data.id
  } else if (dropType === 'before' || dropType === 'after') {
    newParentId = dropNode.data.parentId
  }

  try {
    await updateOrgNode(draggingId, {
      ...draggingNode.data,
      parentId: newParentId,
      sortOrder: dropNode.data.sortOrder || 0
    })
    ElMessage.success('拖拽更新成功')
    await loadTree()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '拖拽更新失败')
    await loadTree()
  }
}

// 人员管理
const memberDialogVisible = ref(false)
const memberOrgId = ref(null)
const memberOrgName = ref('')
const memberList = ref([])

const loadMembers = async (orgId) => {
  try {
    const res = await getOrgUserPositions(orgId)
    memberList.value = res.data || []
  } catch (e) {
    memberList.value = []
  }
}

const handleViewMembers = async (node) => {
  memberOrgId.value = node.id
  memberOrgName.value = node.name
  memberDialogVisible.value = true
  await loadMembers(node.id)
}

const handleSetPrimary = async (row) => {
  try {
    await setPrimaryPosition(row.id)
    ElMessage.success('已设为主职')
    await loadMembers(memberOrgId.value)
    // 同步刷新详情面板
    if (selectedNode.value?.id === memberOrgId.value) {
      await loadMembers(selectedNode.value.id)
    }
  } catch (e) { ElMessage.error('设置失败') }
}

const handleRemoveMember = async (row) => {
  try {
    await removeUserPosition(row.id)
    ElMessage.success('已移除')
    await loadMembers(memberOrgId.value)
    if (selectedNode.value?.id === memberOrgId.value) {
      await loadMembers(selectedNode.value.id)
    }
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
      getUserList({ page: 1, size: 999 }),
      getPositionList({ page: 1, size: 999 }),
      getJobTitleList({ page: 1, size: 999 })
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
    await loadMembers(memberOrgId.value)
    if (selectedNode.value?.id === memberOrgId.value) {
      await loadMembers(selectedNode.value.id)
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '分配失败')
  } finally { assignLoading.value = false }
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
  regionSubType: '',
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
      regionSubType: '',
      unifiedSocialCreditCode: '',
      leaderName: '',
      leaderPhone: '',
      address: '',
      remark: ''
    }
    parentName.value = parentNode.name
    dialogTitle.value = `新增${levelName(parentNode.level + 1, form.value.regionSubType)}（上级：${parentNode.name}）`
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
      regionSubType: '',
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
    regionSubType: node.regionSubType || '',
    unifiedSocialCreditCode: node.unifiedSocialCreditCode || '',
    leaderName: node.leaderName || '',
    leaderPhone: node.leaderPhone || '',
    address: node.address || '',
    remark: node.remark || ''
  }
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
    await loadTree()
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
    await ElMessageBox.confirm(
      `确定删除「${node.name}」及其所有子节点吗？此操作不可恢复。`,
      '删除确认',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteOrgNode(node.id)
    ElMessage.success('删除成功')
    if (selectedNode.value?.id === node.id) {
      selectedNode.value = null
    }
    await loadTree()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadTree()
})
</script>

<style lang="scss" scoped>
.org-page {
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

.org-body {
  flex: 1;
  display: flex;
  padding: 16px;
  gap: 16px;
  overflow: hidden;
}

// 左侧边栏
.org-sidebar {
  width: 360px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .sidebar-header {
    padding: 16px;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .sidebar-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .tree-container {
    flex: 1;
    overflow-y: auto;
    padding: 8px;

    :deep(.el-tree-node__content) {
      height: auto;
      padding: 4px 0;
    }
  }

  .sidebar-footer {
    padding: 12px 16px;
    border-top: 1px solid #ebeef5;
    background: #fafbfc;

    .level-legend {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .legend-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: #606266;

        .legend-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;

          &.level-1 { background: #f56c6c; }
          &.level-2 { background: #409eff; }
          &.level-2-county { background: #e6a23c; }
          &.level-3 { background: #67c23a; }
          &.level-4 { background: #409eff; }
          &.level-5 { background: #909399; }
        }
      }
    }
  }
}

// 自定义树节点
.custom-tree-node {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  width: 100%;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.2s;
  cursor: pointer;

  &:hover {
    background: #f5f7fa;

    .node-actions {
      opacity: 1;
    }
  }

  &.is-active {
    background: #ecf5ff;
    box-shadow: inset 3px 0 0 #409eff;
  }

  .node-main {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    flex: 1;
    min-width: 0;

    .level-indicator {
      width: 28px;
      height: 28px;
      border-radius: 6px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      color: #fff;

      &.level-1 { background: linear-gradient(135deg, #f56c6c, #f89898); }
      &.level-2 { background: linear-gradient(135deg, #409eff, #79bbff); }
      &.level-2-county { background: linear-gradient(135deg, #e6a23c, #f0c78a); }
      &.level-3 { background: linear-gradient(135deg, #67c23a, #95d475); }
      &.level-4 { background: linear-gradient(135deg, #409eff, #79bbff); }
      &.level-5 { background: linear-gradient(135deg, #909399, #b1b3b8); }
    }

    .node-content {
      flex: 1;
      min-width: 0;

      .node-title-row {
        display: flex;
        align-items: center;
        gap: 6px;
        margin-bottom: 2px;

        .node-name {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .level-tag {
          flex-shrink: 0;
        }
      }

      .node-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-wrap: wrap;

        .meta-item {
          font-size: 11px;
          color: #909399;
          display: flex;
          align-items: center;
          gap: 2px;
        }
      }
    }
  }

  .node-actions {
    opacity: 0;
    transition: opacity 0.2s;
    flex-shrink: 0;
    padding-top: 2px;
  }
}

// 右侧详情面板
.org-detail-panel {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  overflow-y: auto;

  .detail-header {
    padding: 24px;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    align-items: center;
    gap: 16px;

    .detail-avatar {
      width: 64px;
      height: 64px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 28px;

      &.level-1 { background: linear-gradient(135deg, #f56c6c, #f89898); }
      &.level-2 { background: linear-gradient(135deg, #409eff, #79bbff); }
      &.level-2-county { background: linear-gradient(135deg, #e6a23c, #f0c78a); }
      &.level-3 { background: linear-gradient(135deg, #67c23a, #95d475); }
      &.level-4 { background: linear-gradient(135deg, #409eff, #79bbff); }
      &.level-5 { background: linear-gradient(135deg, #909399, #b1b3b8); }
    }

    .detail-title-group {
      flex: 1;

      .detail-name {
        margin: 0 0 8px 0;
        font-size: 20px;
        font-weight: 600;
        color: #303133;
      }

      .detail-tags {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }

    .detail-actions {
      display: flex;
      gap: 8px;
    }
  }

  .detail-cards {
    padding: 24px;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .info-card {
      border-radius: 8px;
      border: 1px solid #ebeef5;

      :deep(.el-card__header) {
        padding: 12px 16px;
        border-bottom: 1px solid #ebeef5;
        background: #fafbfc;

        .card-header {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 14px;
          font-weight: 600;
          color: #303133;
        }
      }

      :deep(.el-card__body) {
        padding: 16px;
      }

      .leader-profile {
        display: flex;
        align-items: center;
        gap: 12px;

        .leader-info {
          .leader-name {
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 4px;
          }

          .leader-phone {
            font-size: 13px;
            color: #606266;
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }

      .stats-row {
        display: flex;
        justify-content: space-around;
        padding: 8px 0;

        .stat-item {
          text-align: center;

          .stat-value {
            font-size: 28px;
            font-weight: 700;
            color: #409eff;
            line-height: 1;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
  }
}

.org-detail-empty {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
}

.member-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

:deep(.el-empty__image) {
  display: flex;
  justify-content: center;
}
</style>
