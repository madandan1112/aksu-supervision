<template>
  <div class="report-list-page">
    <div class="page-content">
      <div class="page-header">
        <button class="back-btn" @click="$router.back()">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <h2>{{ userStore.isInspector ? '报告审核' : '合规报告' }}</h2>
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
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18"/><path d="M9 21V9"/></svg>
        <span>暂无报告记录</span>
      </div>

      <div v-for="item in list" :key="item.id" class="report-card">
        <div class="card-top">
          <span class="report-title">{{ item.title || item.reportName || '检查报告' }}</span>
          <span class="report-type">{{ typeText(item.reportType) }}</span>
        </div>
        <div class="card-bottom">
          <span class="status-pill" :class="statusClass(item.status)">{{ statusText(item.status) }}</span>
          <span class="report-time">{{ item.createTime }}</span>
        </div>
        <div v-if="item.expireDate" class="expire-row">有效期至：{{ item.expireDate }}</div>
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
  { label: '待审核', value: 'PENDING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' }
]

const statusClass = (s) => ({
  'PENDING': 'pending',
  'APPROVED': 'done',
  'REJECTED': 'rejected'
}[s] || 'pending')

const statusText = (s) => ({
  'PENDING': '待审核',
  'APPROVED': '已通过',
  'REJECTED': '已驳回'
}[s] || '待审核')

const typeText = (t) => t || '常规检查'

const loadList = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 20 }
    if (currentFilter.value) params.status = currentFilter.value
    const res = await request.get('/enterprise/report/list', { params })
    const d = res.data?.data || res.data
    list.value = d?.content || d?.records || d?.list || (Array.isArray(d) ? d : [])
  } catch (e) {} finally { loading.value = false }
}

const changeFilter = (val) => { currentFilter.value = val; loadList() }
onMounted(loadList)
</script>

<style scoped>
.report-list-page {
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

/* 报告卡片 */
.report-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 16px;
  margin-bottom: 10px;
  box-shadow: var(--shadow-sm);
}
.card-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 10px;
}
.report-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
  flex: 1;
}
.report-type {
  font-size: 11px;
  background: #E0E7FF;
  color: #5B7FFF;
  padding: 2px 8px;
  border-radius: 6px;
  flex-shrink: 0;
}
.card-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.status-pill {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 6px;
}
.status-pill.pending { background: #FEF3C7; color: #D97706; }
.status-pill.done { background: #D1FAE5; color: #059669; }
.status-pill.rejected { background: #FEE2E2; color: #DC2626; }
.report-time {
  font-size: 12px;
  color: var(--text-tertiary);
}
.expire-row {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #F1F5F9;
}

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
