<template>
  <div class="rect-detail-page">
    <div class="page-content">
      <div class="page-header">
        <button class="back-btn" @click="$router.back()">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <h2>整改详情</h2>
        <div style="width:36px"></div>
      </div>

      <div v-if="notice" class="detail-card">
        <div class="detail-header">
          <span class="status-pill" :class="statusClass(notice.status)">{{ statusText(notice.status) }}</span>
        </div>

        <div class="detail-meta">
          <div class="meta-row">
            <span class="meta-label">通知编号</span>
            <span class="meta-value">{{ notice.noticeNo || '-' }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">截止日期</span>
            <span class="meta-value" :class="isOverdue ? 'overdue' : ''">{{ notice.deadline || '-' }}</span>
          </div>
        </div>

        <div class="content-block">
          <div class="block-label">问题描述</div>
          <div class="block-text">{{ notice.issues || '-' }}</div>
        </div>

        <div class="content-block">
          <div class="block-label">整改要求</div>
          <div class="block-text">{{ notice.requirements || '-' }}</div>
        </div>

        <!-- 反馈列表 -->
        <div v-if="feedbacks && feedbacks.length" class="content-block">
          <div class="block-label">整改反馈</div>
          <div v-for="fb in feedbacks" :key="fb.id" class="feedback-card">
            <div class="feedback-text">{{ fb.rectifyMeasures }}</div>
            <div class="feedback-meta">
              <span>{{ fb.createTime }}</span>
              <span class="feedback-status">已提交</span>
            </div>
          </div>
        </div>

        <!-- 验收记录 -->
        <div v-if="acceptances && acceptances.length" class="content-block">
          <div class="block-label">验收记录</div>
          <div v-for="ac in acceptances" :key="ac.id" class="acceptance-card">
            <div class="acceptance-top">
              <span class="accept-result" :class="ac.conclusion === 'PASS' ? 'pass' : 'fail'">
                {{ ac.conclusion === 'PASS' ? '验收通过' : '验收不通过' }}
              </span>
              <span class="accept-time">{{ ac.createTime }}</span>
            </div>
            <div v-if="ac.opinion" class="accept-opinion">{{ ac.opinion }}</div>
          </div>
        </div>

        <!-- 企业用户：提交整改反馈 -->
        <button
          v-if="userStore.isEnterprise && (notice.status === 'ISSUED' || notice.status === '已下发' || notice.status === '待整改')"
          class="btn-gradient"
          @click="showFeedbackForm = true"
        >提交整改反馈</button>

        <!-- 执法人员：验收操作 -->
        <template v-if="userStore.isInspector && (notice.status === 'FEEDBACK_SUBMITTED' || notice.status === '已反馈')">
          <div class="accept-actions">
            <button class="btn-pass" @click="doAccept('PASS')">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>
              验收通过
            </button>
            <button class="btn-fail" @click="doAccept('REJECT')">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              验收不通过
            </button>
          </div>
        </template>
      </div>

      <div v-else-if="!loading" class="empty-card">
        <span>加载中...</span>
      </div>

      <!-- 反馈表单弹窗 -->
      <div v-if="showFeedbackForm" class="modal-overlay" @click.self="showFeedbackForm = false">
        <div class="modal-card">
          <div class="modal-header">
            <h3>提交整改反馈</h3>
            <button class="modal-close" @click="showFeedbackForm = false">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#999" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body">
            <div class="input-group">
              <label>整改措施 <span class="required">*</span></label>
              <div class="textarea-wrap">
                <textarea v-model="feedbackForm.rectifyMeasures" placeholder="请详细描述整改措施" rows="4"></textarea>
              </div>
            </div>
            <div class="input-group">
              <label>备注</label>
              <div class="input-wrap">
                <input v-model="feedbackForm.remark" type="text" placeholder="备注信息（可选）" />
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="showFeedbackForm = false">取消</button>
            <button class="btn-confirm" :disabled="submitting" @click="submitFeedback">
              {{ submitting ? '提交中...' : '提交' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import request from '../utils/request'

const route = useRoute()
const userStore = useUserStore()
const notice = ref(null)
const feedbacks = ref([])
const acceptances = ref([])
const loading = ref(true)
const showFeedbackForm = ref(false)
const submitting = ref(false)

const feedbackForm = ref({ rectifyMeasures: '', remark: '' })

const isOverdue = computed(() => {
  if (!notice.value?.deadline) return false
  return new Date(notice.value.deadline) < new Date()
})

const statusClass = (s) => ({
  'ISSUED': 'pending', '已下发': 'pending', '待整改': 'pending',
  'FEEDBACK_SUBMITTED': 'processing', '已反馈': 'processing',
  'ACCEPTED': 'done', '已通过': 'done',
  'REJECTED': 'rejected', '未通过': 'rejected'
}[s] || 'pending')

const statusText = (s) => ({
  'ISSUED': '待整改', '已下发': '待整改',
  'FEEDBACK_SUBMITTED': '已反馈', '已反馈': '已反馈',
  'ACCEPTED': '已通过', '已通过': '已通过',
  'REJECTED': '未通过', '未通过': '未通过'
}[s] || '待整改')

const loadDetail = async () => {
  try {
    const res = await request.get('/enterprise/rectification/' + route.params.id)
    const d = res.data?.data || res.data
    notice.value = d?.notice || d
    feedbacks.value = d?.feedbacks || []
    acceptances.value = d?.acceptances || []
  } catch (e) {
    console.warn('加载整改详情失败:', e.message)
  } finally {
    loading.value = false
  }
}

const submitFeedback = async () => {
  if (!feedbackForm.value.rectifyMeasures.trim()) {
    window.alert('请输入整改措施')
    return
  }
  submitting.value = true
  try {
    await request.post('/enterprise/rectification/' + route.params.id + '/feedback', feedbackForm.value)
    window.alert('反馈提交成功')
    showFeedbackForm.value = false
    feedbackForm.value = { rectifyMeasures: '', remark: '' }
    loadDetail()
  } catch (e) {
    const msg = e.response?.data?.message || '提交失败'
    window.alert(msg)
  } finally {
    submitting.value = false
  }
}

const doAccept = async (conclusion) => {
  const opinion = window.prompt(conclusion === 'PASS' ? '请输入验收意见（可选）：' : '请输入不通过原因：')
  if (opinion === null) return
  try {
    await request.post('/inspector/rectification/' + route.params.id + '/accept', {
      conclusion, opinion: opinion || ''
    })
    window.alert('验收操作成功')
    loadDetail()
  } catch (e) {
    const msg = e.response?.data?.message || '操作失败'
    window.alert(msg)
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.rect-detail-page {
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
  margin-bottom: 14px;
}
.status-pill {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 6px;
}
.status-pill.pending { background: #FEF3C7; color: #D97706; }
.status-pill.processing { background: #E0E7FF; color: #5B7FFF; }
.status-pill.done { background: #D1FAE5; color: #059669; }
.status-pill.rejected { background: #FEE2E2; color: #DC2626; }

.detail-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 14px;
  border-bottom: 1px solid #F1F5F9;
}
.meta-row {
  display: flex;
  justify-content: space-between;
}
.meta-label {
  font-size: 13px;
  color: var(--text-tertiary);
}
.meta-value {
  font-size: 14px;
  color: var(--text-primary);
}
.meta-value.overdue {
  color: #EF4444;
}

.content-block {
  padding: 14px 0;
  border-top: 1px solid #F1F5F9;
}
.block-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--accent-blue);
  margin-bottom: 8px;
}
.block-text {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.feedback-card {
  background: #F8FAFC;
  border-radius: var(--radius-md);
  padding: 12px;
  margin-bottom: 8px;
}
.feedback-text {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.5;
  margin-bottom: 6px;
}
.feedback-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-tertiary);
}
.feedback-status {
  color: #059669;
}

.acceptance-card {
  background: #F8FAFC;
  border-radius: var(--radius-md);
  padding: 12px;
  margin-bottom: 8px;
}
.acceptance-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}
.accept-result {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 6px;
}
.accept-result.pass { background: #D1FAE5; color: #059669; }
.accept-result.fail { background: #FEE2E2; color: #DC2626; }
.accept-time {
  font-size: 12px;
  color: var(--text-tertiary);
}
.accept-opinion {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 4px;
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

.accept-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}
.btn-pass, .btn-fail {
  flex: 1;
  height: 48px;
  border: none;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #fff;
}
.btn-pass {
  background: linear-gradient(135deg, #10B981, #34D399);
  box-shadow: 0 4px 12px rgba(16,185,129,0.25);
}
.btn-fail {
  background: linear-gradient(135deg, #EF4444, #F87171);
  box-shadow: 0 4px 12px rgba(239,68,68,0.25);
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
}
.modal-card {
  width: 100%;
  max-width: 480px;
  background: #fff;
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  padding: 20px;
  animation: slideUp 0.3s ease;
}
@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.modal-header h3 {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}
.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: #F1F5F9;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.input-group {
  margin-bottom: 14px;
}
.input-group label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 6px;
}
.input-wrap,
.textarea-wrap {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-md);
  padding: 0 14px;
  transition: all 0.2s;
}
.input-wrap:focus-within,
.textarea-wrap:focus-within {
  border-color: var(--accent-blue);
  box-shadow: 0 0 0 3px rgba(91,127,255,0.1);
}
.input-wrap {
  height: 48px;
  display: flex;
  align-items: center;
}
.input-wrap input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 15px;
  outline: none;
}
.textarea-wrap {
  padding: 12px 14px;
}
.textarea-wrap textarea {
  width: 100%;
  border: none;
  background: transparent;
  font-size: 15px;
  outline: none;
  resize: vertical;
  font-family: inherit;
}

.modal-footer {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}
.btn-cancel {
  flex: 1;
  height: 44px;
  background: #F1F5F9;
  color: var(--text-secondary);
  border: none;
  border-radius: var(--radius-md);
  font-size: 14px;
  cursor: pointer;
}
.btn-confirm {
  flex: 1;
  height: 44px;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}
.btn-confirm:disabled {
  opacity: 0.6;
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
