<template>
  <div class="appeal-list-page">
    <div class="page-content">
      <div class="page-header">
        <button class="back-btn" @click="$router.back()">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <h2>{{ userStore.isInspector ? '诉求管理' : '我的诉求' }}</h2>
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
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5"><path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
        <span>暂无诉求记录</span>
      </div>

      <div v-for="item in list" :key="item.id" class="appeal-card" @click="$router.push('/appeal/' + item.id)">
        <div class="card-top">
          <span class="appeal-title">{{ item.title }}</span>
          <span class="type-badge">{{ typeText(item.appealType) }}</span>
        </div>
        <div class="card-bottom">
          <span class="status-pill" :class="statusClass(item.status)">{{ statusText(item.status) }}</span>
          <span class="appeal-time">{{ item.createTime }}</span>
        </div>
      </div>

      <button v-if="userStore.isEnterprise" class="fab-btn" @click="$router.push('/appeal/create')">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
      </button>
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
  { label: '待处理', value: 'PENDING' },
  { label: '已分配', value: 'ASSIGNED' },
  { label: '已处理', value: 'HANDLED' },
  { label: '已评价', value: 'EVALUATED' }
]

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

const loadList = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 20 }
    if (currentFilter.value) params.status = currentFilter.value
    const apiPath = userStore.isInspector ? '/inspector/appeal/list' : '/appeal/list'
    const res = await request.get(apiPath, { params })
    const d = res.data?.data || res.data
    list.value = d?.content || d?.list || d?.records || (Array.isArray(d) ? d : [])
  } catch (e) {} finally { loading.value = false }
}

const changeFilter = (val) => { currentFilter.value = val; loadList() }
onMounted(loadList)
</script>

<style scoped>
.appeal-list-page {
  min-height: 100vh;
  background: var(--bg-primary);
  padding-bottom: 90px;
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

/* 诉求卡片 */
.appeal-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 16px;
  margin-bottom: 10px;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all 0.2s;
}
.appeal-card:active {
  transform: scale(0.98);
}
.card-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 10px;
}
.appeal-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
  flex: 1;
}
.type-badge {
  flex-shrink: 0;
  font-size: 11px;
  background: #F1F5F9;
  color: var(--text-tertiary);
  padding: 2px 8px;
  border-radius: 6px;
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
.status-pill.processing { background: #DBEAFE; color: #2563EB; }
.status-pill.done { background: #D1FAE5; color: #059669; }
.status-pill.evaluated { background: #EDE9FE; color: #7C3AED; }
.appeal-time {
  font-size: 12px;
  color: var(--text-tertiary);
}

/* FAB */
.fab-btn {
  position: fixed;
  bottom: 90px;
  right: 20px;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(91,127,255,0.3);
  z-index: 100;
}
.fab-btn:active {
  transform: scale(0.95);
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
