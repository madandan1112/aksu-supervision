<template>
  <div class="page-container">
    <el-page-header @back="$router.back()" :content="'任务 #' + taskData.id" class="page-header" />

    <el-row :gutter="16">
      <!-- 左侧：任务信息 -->
      <el-col :span="16">
        <el-card shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">任务信息</div>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="任务编号">{{ taskData.id }}</el-descriptions-item>
              <el-descriptions-item label="任务类型"><el-tag size="small">{{ taskData.taskType }}</el-tag></el-descriptions-item>
              <el-descriptions-item label="任务标题" :span="2">{{ taskData.title }}</el-descriptions-item>
              <el-descriptions-item label="任务说明" :span="2">{{ taskData.description || '无' }}</el-descriptions-item>
              <el-descriptions-item label="当前状态">
                <el-tag :type="statusTypeMap[taskData.status] || 'info'">{{ statusLabel[taskData.status] || taskData.status }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="创建人">{{ taskData.createdBy || '-' }}</el-descriptions-item>
              <el-descriptions-item label="计划开始">{{ formatTime(taskData.plannedStartTime) }}</el-descriptions-item>
              <el-descriptions-item label="计划结束">{{ formatTime(taskData.plannedEndTime) }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatTime(taskData.createTime) }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ formatTime(taskData.updateTime) }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <!-- 检查企业 -->
        <el-card shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">检查对象</div>
            <el-table :data="taskEnterprises" size="small" v-if="taskEnterprises.length > 0">
              <el-table-column prop="enterpriseName" label="企业名称" min-width="160" show-overflow-tooltip />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'PENDING'" size="small">{{ row.status === 'COMPLETED' ? '已完成' : '待检查' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="inspectorName" label="检查人" width="100" />
            </el-table>
            <el-empty v-else description="暂未分配检查对象" :image-size="40" />
          </div>
        </el-card>

        <!-- 终止信息 -->
        <el-card v-if="taskData.terminateReason" shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">终止信息</div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="终止原因">{{ taskData.terminateReason }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：操作 -->
      <el-col :span="8">
        <el-card shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">任务操作</div>
            <div class="action-buttons">
              <el-button v-if="taskData.status === 'PENDING' || taskData.status === 'IN_PROGRESS'" type="primary" @click="handleEdit" style="width:100%">编辑任务</el-button>
              <el-button v-if="taskData.status === 'IN_PROGRESS'" type="danger" @click="terminateDialogVisible = true" style="width:100%">终止任务</el-button>
              <el-button type="info" @click="$router.back()" style="width:100%">返回列表</el-button>
            </div>
          </div>
        </el-card>

        <!-- 任务进度 -->
        <el-card shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">任务进度</div>
            <el-steps :active="activeStep" align-center>
              <el-step title="待执行" />
              <el-step title="进行中" />
              <el-step title="已完成" />
            </el-steps>
          </div>
        </el-card>

        <!-- 统计概览 -->
        <el-card shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">统计概览</div>
            <div class="stat-grid">
              <div class="stat-item">
                <div class="stat-val">{{ taskEnterprises.length }}</div>
                <div class="stat-lbl">检查企业</div>
              </div>
              <div class="stat-item">
                <div class="stat-val">{{ completedCount }}</div>
                <div class="stat-lbl">已完成</div>
              </div>
              <div class="stat-item">
                <div class="stat-val">{{ pendingCount }}</div>
                <div class="stat-lbl">待检查</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 终止对话框 -->
    <el-dialog v-model="terminateDialogVisible" title="终止任务" width="450px">
      <p>确定要终止该任务吗？此操作不可撤销。</p>
      <el-form :model="terminateForm" label-width="80px" style="margin-top: 16px">
        <el-form-item label="终止原因"><el-input v-model="terminateForm.reason" type="textarea" :rows="3" placeholder="请输入终止原因" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="terminateDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleTerminate">确认终止</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTaskList, terminateTask } from '@/api/task'

const route = useRoute()
const router = useRouter()
const taskData = ref({})
const taskEnterprises = ref([])
const terminateDialogVisible = ref(false)
const terminateForm = ref({ reason: '' })

const statusTypeMap = { PENDING: 'warning', IN_PROGRESS: '', COMPLETED: 'success', TERMINATED: 'danger' }
const statusLabel = { PENDING: '待执行', IN_PROGRESS: '进行中', COMPLETED: '已完成', TERMINATED: '已终止' }

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const activeStep = computed(() => {
  const s = taskData.value.status
  if (s === 'PENDING') return 0
  if (s === 'IN_PROGRESS') return 1
  if (s === 'COMPLETED') return 2
  return 0
})

const completedCount = computed(() => taskEnterprises.value.filter(e => e.status === 'COMPLETED').length)
const pendingCount = computed(() => taskEnterprises.value.filter(e => e.status !== 'COMPLETED').length)

const handleEdit = () => { router.push(`/task/${taskData.value.id}/edit`) }

const handleTerminate = async () => {
  if (!terminateForm.value.reason) { ElMessage.warning('请输入终止原因'); return }
  try {
    await terminateTask(taskData.value.id, { reason: terminateForm.value.reason })
    ElMessage.success('任务已终止')
    terminateDialogVisible.value = false
    fetchDetail()
  } catch (e) { ElMessage.error('操作失败') }
}

const fetchDetail = async () => {
  try {
    const res = await getTaskList({ page: 1, size: 1000 })
    const list = res.data?.list || []
    taskData.value = list.find(t => t.id === Number(route.params.id)) || {}
    // 解析企业列表（如果有）
    if (taskData.value.enterpriseIds) {
      try {
        taskEnterprises.value = JSON.parse(taskData.value.enterpriseIds).map((id, idx) => ({ id, enterpriseName: `企业#${id}`, status: 'PENDING', inspectorName: '-' }))
      } catch { taskEnterprises.value = [] }
    }
  } catch (e) { ElMessage.warning('加载失败') }
}

onMounted(() => fetchDetail())
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 16px; }
.detail-card { margin-bottom: 16px; }
.action-buttons { display: flex; flex-direction: column; gap: 10px; }
.stat-grid { display: flex; gap: 16px;
  .stat-item { flex: 1; text-align: center; padding: 12px 0; background: #f5f7fa; border-radius: 6px;
    .stat-val { font-size: 24px; font-weight: 700; color: #1A73E8; }
    .stat-lbl { font-size: 12px; color: #909399; margin-top: 4px; }
  }
}
</style>
