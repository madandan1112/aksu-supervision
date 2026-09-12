<template>
  <div class="page-container">
    <el-page-header @back="$router.back()" :content="'诉求 #' + appealData.id" class="page-header" />

    <el-row :gutter="16">
      <!-- 左侧：诉求内容 -->
      <el-col :span="16">
        <!-- 诉求信息 -->
        <el-card shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">诉求信息</div>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="诉求编号">{{ appealData.id }}</el-descriptions-item>
              <el-descriptions-item label="诉求类型">
                <el-tag size="small">{{ appealData.appealType }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="诉求标题" :span="2">{{ appealData.title }}</el-descriptions-item>
              <el-descriptions-item label="诉求内容" :span="2">{{ appealData.content }}</el-descriptions-item>
              <el-descriptions-item label="涉及领域">{{ appealData.relatedField || '-' }}</el-descriptions-item>
              <el-descriptions-item label="关联科室">{{ appealData.relatedDept || '-' }}</el-descriptions-item>
              <el-descriptions-item label="当前状态">
                <el-tag :type="statusTypeMap[appealData.status] || 'info'">{{ statusLabel[appealData.status] || appealData.status }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatTime(appealData.createTime) }}</el-descriptions-item>
              <el-descriptions-item label="处理人">{{ appealData.assignedTo || '未分配' }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ formatTime(appealData.updateTime) }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <!-- 佐证材料 -->
        <el-card v-if="appealData.attachments" shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">佐证材料</div>
            <div class="attachments-grid">
              <div v-for="(item, idx) in parseAttachments(appealData.attachments)" :key="idx" class="attachment-item">
                <el-icon :size="32" color="#C8102E"><Document /></el-icon>
                <div class="attachment-name">{{ item }}</div>
              </div>
            </div>
            <el-empty v-if="!appealData.attachments" description="无附件" :image-size="40" />
          </div>
        </el-card>

        <!-- 处理结果 -->
        <el-card v-if="appealData.handleResult" shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">处理结果</div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="处理结果">{{ appealData.handleResult }}</el-descriptions-item>
              <el-descriptions-item label="处理时间">{{ formatTime(appealData.handleTime) }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <!-- 评价信息 -->
        <el-card v-if="appealData.satisfaction" shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">满意度评价</div>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="满意度">
                <el-rate v-model="appealData.satisfaction" disabled />
              </el-descriptions-item>
              <el-descriptions-item label="评价时间">{{ formatTime(appealData.evaluateTime) }}</el-descriptions-item>
              <el-descriptions-item label="评价内容" :span="2">{{ appealData.evaluateComment || '无' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：操作 -->
      <el-col :span="8">
        <!-- 分配/分流 -->
        <el-card v-if="appealData.status === 'PENDING'" shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">诉求分流</div>
            <div class="suggest-box">
              <el-icon color="#C8102E"><InfoFilled /></el-icon>
              <span>系统建议分流至：<strong>{{ suggestDept }}</strong></span>
            </div>
            <el-form :model="assignForm" :rules="assignRules" ref="assignFormRef" label-width="80px">
              <el-form-item label="分配科室" prop="assignedDept">
                <el-select v-model="assignForm.assignedDept" placeholder="请选择科室" style="width: 100%">
                  <el-option label="食品安全监管科" value="食品安全监管科" />
                  <el-option label="特种设备安全监察科" value="特种设备安全监察科" />
                  <el-option label="产品质量安全监管科" value="产品质量安全监管科" />
                  <el-option label="知识产权科" value="知识产权科" />
                  <el-option label="行政许可科" value="行政许可科" />
                  <el-option label="综合执法科" value="综合执法科" />
                </el-select>
              </el-form-item>
              <el-form-item label="处理人" prop="assignedTo">
                <el-input v-model="assignForm.assignedTo" placeholder="请输入处理人姓名" />
              </el-form-item>
              <el-form-item label="分流意见">
                <el-input v-model="assignForm.remark" type="textarea" :rows="3" placeholder="请输入分流意见" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitAssign">确认分流</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>

        <!-- 处理 -->
        <el-card v-if="appealData.status === 'ASSIGNED' || appealData.status === 'HANDLING'" shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">处理诉求</div>
            <el-form :model="processForm" :rules="processRules" ref="processFormRef" label-width="80px">
              <el-form-item label="处理结果" prop="handleResult">
                <el-input v-model="processForm.handleResult" type="textarea" :rows="4" placeholder="请输入处理结果" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitProcess">提交处理</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>

        <!-- 状态跟踪 -->
        <el-card shadow="never" class="detail-card">
          <div class="detail-section">
            <div class="section-title">状态跟踪</div>
            <el-timeline>
              <el-timeline-item type="primary" placement="top">
                <div class="record-item">
                  <div class="record-action">提交诉求</div>
                  <div class="record-operator">{{ formatTime(appealData.createTime) }}</div>
                </div>
              </el-timeline-item>
              <el-timeline-item v-if="appealData.assignedTo" type="primary" placement="top">
                <div class="record-item">
                  <div class="record-action">分流分配</div>
                  <div class="record-operator">处理人：{{ appealData.assignedTo }}</div>
                </div>
              </el-timeline-item>
              <el-timeline-item v-if="appealData.handleResult" type="success" placement="top">
                <div class="record-item">
                  <div class="record-action">处理完成</div>
                  <div class="record-operator">{{ formatTime(appealData.handleTime) }}</div>
                </div>
              </el-timeline-item>
              <el-timeline-item v-if="appealData.satisfaction" type="info" placement="top">
                <div class="record-item">
                  <div class="record-action">已评价</div>
                  <div class="record-operator">满意度：{{ appealData.satisfaction }}星</div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAppealDetail, assignAppeal, handleAppeal } from '@/api/appeal'

const route = useRoute()
const processFormRef = ref(null)
const assignFormRef = ref(null)

const statusTypeMap = { PENDING: 'warning', ASSIGNED: 'primary', HANDLING: '', HANDLED: 'success', EVALUATED: 'info' }
const statusLabel = { PENDING: '待分流', ASSIGNED: '已分配', HANDLING: '处理中', HANDLED: '已办结', EVALUATED: '已评价' }

const appealData = ref({})
const assignForm = reactive({ assignedTo: '', assignedDept: '', remark: '' })
const assignRules = {
  assignedDept: [{ required: true, message: '请选择科室', trigger: 'change' }],
  assignedTo: [{ required: true, message: '请输入处理人', trigger: 'blur' }]
}
const processForm = reactive({ handleResult: '' })
const processRules = { handleResult: [{ required: true, message: '请输入处理结果', trigger: 'blur' }] }

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }
const parseAttachments = (str) => { if (!str) return []; try { return JSON.parse(str) } catch { return str.split(',') } }

const suggestDept = computed(() => {
  const d = appealData.value
  const map = { '食品安全': '食品安全监管科', '特种设备': '特种设备安全监察科', '产品质量': '产品质量安全监管科', '知识产权': '知识产权科', '许可': '行政许可科', '政策': '行政许可科' }
  for (const [key, val] of Object.entries(map)) {
    if (d.appealType?.includes(key) || d.title?.includes(key)) return val
  }
  return '综合执法科'
})

const submitAssign = async () => {
  const valid = await assignFormRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await assignAppeal(appealData.value.id, { assignedTo: assignForm.assignedTo, remark: `[${assignForm.assignedDept}] ${assignForm.remark}` })
    ElMessage.success('分流成功')
    fetchDetail()
  } catch (e) { ElMessage.error('分流失败') }
}

const submitProcess = async () => {
  const valid = await processFormRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await handleAppeal(appealData.value.id, { handleResult: processForm.handleResult })
    ElMessage.success('处理提交成功')
    fetchDetail()
  } catch (e) { ElMessage.error('处理失败') }
}

const fetchDetail = async () => {
  try {
    const res = await getAppealDetail(route.params.id)
    appealData.value = res.data || {}
    // 自动填充分流建议
    if (appealData.value.status === 'PENDING' && !assignForm.assignedDept) {
      assignForm.assignedDept = suggestDept.value
    }
  } catch (e) { ElMessage.warning('加载诉求详情失败') }
}

onMounted(() => fetchDetail())
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 16px; }
.detail-card { margin-bottom: 16px; }
.suggest-box {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 14px; background: #e8f0fe; border-radius: 6px; margin-bottom: 16px; font-size: 14px;
}
.attachments-grid {
  display: flex; flex-wrap: wrap; gap: 12px;
  .attachment-item {
    width: 100px; text-align: center;
    .attachment-name { font-size: 12px; color: #606266; margin-top: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  }
}
.record-item {
  .record-action { font-weight: 600; color: #303133; }
  .record-operator { font-size: 13px; color: #909399; margin-top: 4px; }
}
</style>
