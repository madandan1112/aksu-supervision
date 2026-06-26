<template>
  <div class="rect-list-page">
    <div class="page-content">
      <div class="page-header">
        <button class="back-btn" @click="$router.back()">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <h2>{{ userStore.isInspector ? '整改验收' : '整改列表' }}</h2>
        <div style="width:36px"></div>
      </div>

      <!-- 药丸筛选项 -->
      <div class="pill-filter">
        <div
          v-for="f in filters"
          :key="f.value"
          class="filter-pill"
          :class="{ active: currentFilter === f.value }"
          @click="changeFilter(f.value)"
        >{{ f.label }}</div>
      </div>

      <div v-if="list.length === 0 && !loading" class="empty-card">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>
        <span>暂无整改记录</span>
      </div>

      <div v-for="item in list" :key="item.id" class="rect-card" @click="$router.push('/rectification/' + item.id)">
        <div class="card-top">
          <span class="rect-title">{{ item.issues || item.noticeNo || '整改通知' }}</span>
          <span class="rect-deadline">截止 {{ item.deadline || '-' }}</span>
        </div>
        <div class="card-bottom">
          <span class="status-pill" :class="statusClass(item.status)">{{ statusText(item.status) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import request from '../utils/request'

const userStore = useUserStore()
const list = ref([])
const loading = ref(false)
const currentFilter = ref('')

const filters = [
  { label: '全部', value: '' },
  { label: '待整改', value: 'ISSUED' },
  { label: '已反馈', value: 'FEEDBACK_SUBMITTED' },
  { label: '已通过', value: 'ACCEPTED' },
  { label: '未通过', value: 'REJECTED' }
]

const statusClass = (s) => ({
  'ISSUED': 'pending', '已下发': 'pending', '待整改': 'pending',
  'FEEDBACK_SUBMITTED': 'processing', '已反馈': 'processing', '整改中': 'processing',
  'ACCEPTED': 'done', '已通过': 'done', '已完成': 'done',
  'REJECTED': 'rejected', '未通过': 'rejected'
}[s] || 'pending')

const statusText = (s) => ({
  'ISSUED': '待整改', '已下发': '待整改', '待整改': '待整改',
  'FEEDBACK_SUBMITTED': '已反馈', '已反馈': '已反馈', '整改中': '整改中',
  'ACCEPTED': '已通过', '已通过': '已通过', '已完成': '已完成',
  'REJECTED': '未通过', '未通过': '未通过'
}[s] || '待整改')

const loadList = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 20 }
    if (currentFilter.value) params.status = currentFilter.value
    const apiPath = userStore.isInspector ? '/inspector/rectification/pending' : '/enterprise/rectification/list'
    const res = await request.get(apiPath, { params })
    const d = res.data?.data || res.data
    list.value = d?.list || d?.content || d?.records || (Array.isArray(d) ? d : [])
  } catch (e) {} finally { loading.value = false }
}

const changeFilter = (val) => { currentFilter.value = val; loadList() }
onMounted(loadList)
</script>

<style scoped>
.rect-list-page {
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

/* 药丸筛选 */
.pill-filter {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  overflow-x: auto;
  padding-bottom: 4px;
}
.filter-pill {
  flex-shrink: 0;
  padding: 8px 18px;
  border-radius: var(--radius-pill);
  background: #fff;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transition: all 0.2s;
}
.filter-pill.active {
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  box-shadow: 0 4px 12px rgba(91,127,255,0.25);
}

/* 整改卡片 */
.rect-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 16px;
  margin-bottom: 10px;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all 0.2s;
}
.rect-card:active {
  transform: scale(0.98);
}
.card-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 10px;
}
.rect-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
  flex: 1;
}
.rect-deadline {
  font-size: 12px;
  color: #EF4444;
  flex-shrink: 0;
}
.card-bottom {
  display: flex;
  align-items: center;
}
.status-pill {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 6px;
}
.status-pill.pending { background: #FEF3C7; color: #D97706; }
.status-pill.processing { background: #E0E7FF; color: #5B7FFF; }
.status-pill.done { background: #D1FAE5; color: #059669; }
.status-pill.rejected { background: #FEE2E2; color: #DC2626; }

.empty-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 40px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #CBD5E1;
  font-size: 14px;
}
</style>
