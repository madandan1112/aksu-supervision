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
          <button class="btn-gradient" @click="handleAppeal">处理诉求</button>
        </template>
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

const handleAppeal = async () => {
  const handleResult = window.prompt('请输入处理结果：')
  if (!handleResult) return
  try {
    await request.put('/admin/appeal/' + route.params.id + '/handle', null, {
      params: { handleResult }
    })
    window.alert('处理成功')
    loadDetail()
  } catch (e) {
    const msg = e.response?.data?.message || '处理失败'
    window.alert(msg)
  }
}

onMounted(loadDetail)
</script>

<style scoped>
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
.status-pill.processing { background: #E0E7FF; color: #5B7FFF; }
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
