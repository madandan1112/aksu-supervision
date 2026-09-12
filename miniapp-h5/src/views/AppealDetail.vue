<template>
  <div class="appeal-detail-page">
    <div class="page-content">
      <div class="page-header">
        <button class="back-btn" @click="$router.back()">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <h2>诉求详情</h2>
        <div style="width:36px"></div>
      </div>

      <div v-if="detail" class="detail-card">
        <div class="detail-header">
          <span class="status-pill" :class="statusClass(detail.status)">{{ statusText(detail.status) }}</span>
          <span class="type-badge">{{ typeText(detail.appealType) }}</span>
        </div>

        <h3 class="detail-title">{{ detail.title }}</h3>
        <div class="detail-time">{{ detail.createTime }}</div>

        <div class="content-block">
          <div class="block-label">诉求内容</div>
          <div class="block-text">{{ detail.content }}</div>
        </div>

        <div v-if="detail.relatedFields" class="content-block">
          <div class="block-label">关联领域</div>
          <div class="block-text">{{ detail.relatedFields }}</div>
        </div>

        <div v-if="detail.handleResult" class="content-block">
          <div class="block-label">处理结果</div>
          <div class="block-text">{{ detail.handleResult }}</div>
        </div>

        <template v-if="userStore.isInspector && (detail.status === 'PENDING' || detail.status === 'ASSIGNED' || detail.status === '待处理' || detail.status === '已分配')">
          <button class="btn-gradient" @click="handleVisible = true">处理诉求</button>
        </template>
      </div>

      <!-- 处理诉求弹层 -->
      <div v-if="handleVisible" class="modal-mask" @click.self="handleVisible = false">
        <div class="modal-sheet">
          <div class="modal-header">
            <h3>处理诉求</h3>
            <button class="modal-close" @click="handleVisible = false">×</button>
          </div>
          <div class="modal-body">
            <div class="form-label">处理结果 <span class="required">*</span></div>
            <textarea v-model="handleResultText" rows="4" placeholder="请输入处理结果说明" class="handle-textarea"></textarea>
          </div>
          <div class="modal-footer">
            <button class="btn-plain" @click="handleVisible = false">取消</button>
            <button class="btn-gradient" :disabled="handleSubmitting" @click="submitHandle">{{ handleSubmitting ? '提交中...' : '提交' }}</button>
          </div>
        </div>
      </div>

      <div v-else-if="!loading" class="empty-card">
        <span>加载中...</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import request from '../utils/request'

const route = useRoute()
const userStore = useUserStore()
const detail = ref(null)
const loading = ref(true)

const statusClass = (s) => ({
  'PENDING': 'pending', '待处理': 'pending',
  'ASSIGNED': 'processing', '已分配': 'processing',
  'HANDLING': 'processing', '处理中': 'processing',
  'HANDLED': 'done', '已处理': 'done',
  'EVALUATED': 'evaluated', '已评价': 'evaluated'
}[s] || 'pending')

const statusText = (s) => ({
  'PENDING': '待处理', '待处理': '待处理',
  'ASSIGNED': '已分配', '已分配': '已分配',
  'HANDLING': '处理中', '处理中': '处理中',
  'HANDLED': '已处理', '已处理': '已处理',
  'EVALUATED': '已评价', '已评价': '已评价'
}[s] || '待处理')

const typeText = (t) => ({
  'COMPLAINT': '投诉举报', 'CONSULT': '咨询建议', 'OTHER': '其他'
}[t] || '')

const loadDetail = async () => {
  try {
    const res = await request.get('/appeal/' + route.params.id)
    detail.value = res.data?.data || res.data
  } catch (e) {
    console.warn('加载诉求详情失败:', e.message)
  } finally {
    loading.value = false
  }
}

const handleVisible = ref(false)
const handleResultText = ref('')
const handleSubmitting = ref(false)

const submitHandle = async () => {
  const handleResult = handleResultText.value?.trim()
  if (!handleResult) { window.alert('请填写处理结果'); return }
  handleSubmitting.value = true
  try {
    await request.put('/inspector/appeal/' + route.params.id + '/handle', null, {
      params: { handleResult }
    })
    window.alert('处理成功')
    handleVisible.value = false
    handleResultText.value = ''
    loadDetail()
  } catch (e) {
    const msg = e.response?.data?.message || '处理失败'
    window.alert(msg)
  } finally {
    handleSubmitting.value = false
  }
}

onMounted(loadDetail)
</script>

<style scoped>
/* 处理诉求弹层 */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 100;
}
.modal-sheet {
  width: 100%;
  max-width: 480px;
  background: #fff;
  border-radius: 16px 16px 0 0;
  padding: 20px 16px calc(16px + env(safe-area-inset-bottom));
}
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.modal-header h3 { font-size: 16px; font-weight: 600; margin: 0; }
.modal-close {
  border: none; background: #F1F5F9; width: 28px; height: 28px;
  border-radius: 50%; font-size: 16px; color: #64748B; cursor: pointer;
}
.form-label { font-size: 13px; color: #475569; margin-bottom: 8px; }
.required { color: #EF4444; }
.handle-textarea {
  width: 100%; border: 1px solid #E2E8F0; border-radius: 8px;
  padding: 10px; font-size: 14px; box-sizing: border-box; resize: vertical;
}
.handle-textarea:focus { outline: none; border-color: #2563EB; }
.modal-footer { display: flex; gap: 10px; margin-top: 16px; }
.btn-plain {
  flex: 1; padding: 12px; border: 1px solid #E2E8F0; border-radius: 8px;
  background: #fff; color: #475569; font-size: 15px; cursor: pointer;
}
.btn-gradient { flex: 1; }

.appeal-detail-page {
  min-height: 100vh;
  background: var(--bg-primary);
  padding-bottom: 80px;
}
.page-content {
  max-width: 480px;
  margin: 0 auto;
  padding: 12px 16px;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.back-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: #fff;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-secondary);
  box-shadow: var(--shadow-sm);
}
.page-header h2 {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.detail-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}
.detail-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
}
.status-pill {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 6px;
}
.status-pill.pending { background: #FEF3C7; color: #D97706; }
.status-pill.processing { background: #DBEAFE; color: #2563EB; }
.status-pill.done { background: #D1FAE5; color: #059669; }
.status-pill.evaluated { background: #EDE9FE; color: #7C3AED; }
.type-badge {
  font-size: 11px;
  background: #F1F5F9;
  color: var(--text-tertiary);
  padding: 2px 8px;
  border-radius: 6px;
}
.detail-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}
.detail-time {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-bottom: 16px;
}
.content-block {
  padding: 14px 0;
  border-top: 1px solid #F1F5F9;
}
.block-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--accent-blue);
  margin-bottom: 6px;
}
.block-text {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}
.btn-gradient {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 20px;
  box-shadow: 0 4px 16px rgba(91,127,255,0.25);
}
.btn-gradient:active {
  transform: scale(0.98);
}
.empty-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 40px;
  text-align: center;
  color: #CBD5E1;
  font-size: 14px;
}
</style>
