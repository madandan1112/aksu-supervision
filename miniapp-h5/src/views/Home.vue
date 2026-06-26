<template>
  <div class="home-page">
    <div class="page-content">
      <!-- 顶部欢迎 + 渐变状态卡 -->
      <div class="hero-section">
        <div class="welcome-row">
          <div class="welcome-text">
            <span class="greeting">{{ greeting }}</span>
            <h2 class="user-name">{{ displayName }}</h2>
          </div>
          <div class="avatar" @click="$router.push('/profile')">
            {{ displayName?.charAt(0) || '用' }}
          </div>
        </div>

        <!-- 渐变状态卡 -->
        <div class="status-card" :class="userStore.isInspector ? 'inspector' : 'enterprise'">
          <div class="status-info">
            <span class="status-label">{{ userStore.isInspector ? '今日检查任务' : '企业状态' }}</span>
            <span class="status-value">{{ userStore.isInspector ? '3 项待完成' : '正常运营' }}</span>
          </div>
          <div class="status-icon">
            <svg v-if="userStore.isInspector" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" opacity="0.9">
              <path d="M3 7V5a2 2 0 0 1 2-2h2"/><path d="M17 3h2a2 2 0 0 1 2 2v2"/><path d="M21 17v2a2 2 0 0 1-2 2h-2"/><path d="M7 21H5a2 2 0 0 1-2-2v-2"/><rect x="7" y="7" width="10" height="10" rx="1"/>
            </svg>
            <svg v-else width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" opacity="0.9">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
          </div>
        </div>
      </div>

      <!-- 通知提示条 -->
      <div v-if="userStore.isInspector" class="notice-bar" @click="$router.push('/appeal')">
        <div class="notice-dot"></div>
        <span class="notice-text">您有 {{ pendingAppeals }} 条待处理诉求</span>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#999" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
      </div>
      <div v-else class="notice-bar" @click="$router.push('/rectification')">
        <div class="notice-dot" style="background:#EF4444"></div>
        <span class="notice-text">您有 {{ pendingRects }} 条待整改通知</span>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#999" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
      </div>

      <!-- 快捷功能入口网格 -->
      <div class="section-title">快捷入口</div>
      <div class="feature-grid">
        <!-- 企业用户功能 -->
        <template v-if="userStore.isEnterprise">
          <div class="feature-card" @click="$router.push('/appeal/create')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #E0E7FF, #C7D2FE);">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#5B7FFF" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
            </div>
            <span class="feature-name">诉求提交</span>
            <span class="feature-desc">发起新诉求</span>
          </div>
          <div class="feature-card" @click="$router.push('/appeal')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #FEF3C7, #FDE68A);">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
            </div>
            <span class="feature-name">我的诉求</span>
            <span class="feature-desc">查看进度</span>
          </div>
          <div class="feature-card" @click="$router.push('/rectification')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #D1FAE5, #A7F3D0);">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>
            </div>
            <span class="feature-name">整改反馈</span>
            <span class="feature-desc">提交整改</span>
          </div>
          <div class="feature-card" @click="$router.push('/report')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #E0E7FF, #C7D2FE);">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#7B61FF" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18"/><path d="M9 21V9"/></svg>
            </div>
            <span class="feature-name">合规报告</span>
            <span class="feature-desc">查看报告</span>
          </div>
        </template>

        <!-- 执法人员功能 -->
        <template v-if="userStore.isInspector">
          <div class="feature-card" @click="$router.push('/scan')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #E0E7FF, #C7D2FE);">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#5B7FFF" stroke-width="2"><path d="M3 7V5a2 2 0 0 1 2-2h2"/><path d="M17 3h2a2 2 0 0 1 2 2v2"/><path d="M21 17v2a2 2 0 0 1-2 2h-2"/><path d="M7 21H5a2 2 0 0 1-2-2v-2"/><rect x="7" y="7" width="10" height="10" rx="1"/></svg>
            </div>
            <span class="feature-name">扫码查企</span>
            <span class="feature-desc">查询企业</span>
          </div>
          <div class="feature-card" @click="$router.push('/appeal')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #FEF3C7, #FDE68A);">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
            </div>
            <span class="feature-name">诉求处理</span>
            <span class="feature-desc">处理诉求</span>
          </div>
          <div class="feature-card" @click="$router.push('/rectification')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #D1FAE5, #A7F3D0);">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>
            </div>
            <span class="feature-name">整改验收</span>
            <span class="feature-desc">验收整改</span>
          </div>
          <div class="feature-card" @click="$router.push('/report')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #FCE7F3, #FBCFE8);">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#EC4899" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18"/><path d="M9 21V9"/></svg>
            </div>
            <span class="feature-name">报告审核</span>
            <span class="feature-desc">审核报告</span>
          </div>
        </template>
      </div>

      <!-- 最新动态列表 -->
      <div class="section-title">{{ userStore.isInspector ? '待处理诉求' : '我的诉求' }}</div>
      <div class="list-card">
        <div v-if="appealList.length === 0" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5"><path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
          <span>暂无诉求记录</span>
        </div>
        <div v-for="item in appealList.slice(0,3)" :key="item.id" class="list-item" @click="$router.push('/appeal/' + item.id)">
          <div class="list-dot" :class="appealStatusClass(item.status)"></div>
          <div class="list-body">
            <span class="list-title">{{ item.title || '诉求' }}</span>
            <span class="list-time">{{ item.createTime || '' }}</span>
          </div>
          <span class="list-status" :class="appealStatusClass(item.status)">{{ appealStatusText(item.status) }}</span>
        </div>
      </div>

      <!-- 整改/报告快捷入口 -->
      <div class="section-title">{{ userStore.isInspector ? '待验收整改' : '整改通知' }}</div>
      <div class="list-card">
        <div v-if="rectList.length === 0" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>
          <span>暂无整改记录</span>
        </div>
        <div v-for="item in rectList.slice(0,3)" :key="item.id" class="list-item" @click="$router.push('/rectification/' + item.id)">
          <div class="list-dot" :class="rectStatusClass(item.status)"></div>
          <div class="list-body">
            <span class="list-title">{{ item.issues || item.noticeNo || '整改通知' }}</span>
            <span class="list-time">截止：{{ item.deadline || '-' }}</span>
          </div>
          <span class="list-status" :class="rectStatusClass(item.status)">{{ rectStatusText(item.status) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import request from '../utils/request'

const userStore = useUserStore()
const appealList = ref([])
const rectList = ref([])

const displayName = computed(() => {
  return userStore.userInfo?.realName || userStore.userInfo?.username || '用户'
})

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return '上午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const pendingAppeals = computed(() => appealList.value.filter(i => ['PENDING','ASSIGNED','待处理','已分配'].includes(i.status)).length)
const pendingRects = computed(() => rectList.value.filter(i => ['ISSUED','已下发','待整改'].includes(i.status)).length)

const appealStatusClass = (s) => ({
  'PENDING': 'pending', '待处理': 'pending',
  'ASSIGNED': 'processing', '已分配': 'processing',
  'HANDLING': 'processing', '处理中': 'processing',
  'HANDLED': 'done', '已处理': 'done',
  'EVALUATED': 'done', '已评价': 'done'
}[s] || 'pending')

const appealStatusText = (s) => ({
  'PENDING': '待处理', '待处理': '待处理',
  'ASSIGNED': '已分配', '已分配': '已分配',
  'HANDLING': '处理中', '处理中': '处理中',
  'HANDLED': '已处理', '已处理': '已处理',
  'EVALUATED': '已评价', '已评价': '已评价'
}[s] || '待处理')

const rectStatusClass = (s) => ({
  'ISSUED': 'pending', '已下发': 'pending', '待整改': 'pending',
  'FEEDBACK_SUBMITTED': 'processing', '已反馈': 'processing', '整改中': 'processing',
  'ACCEPTED': 'done', '已通过': 'done', '已完成': 'done',
  'REJECTED': 'rejected', '未通过': 'rejected'
}[s] || 'pending')

const rectStatusText = (s) => ({
  'ISSUED': '待整改', '已下发': '待整改',
  'FEEDBACK_SUBMITTED': '已反馈', '已反馈': '已反馈',
  'ACCEPTED': '已通过', '已通过': '已通过',
  'REJECTED': '未通过', '未通过': '未通过'
}[s] || '待整改')

const loadData = async () => {
  try {
    const appealPath = userStore.isInspector ? '/admin/appeal/list' : '/appeal/list'
    const rectPath = userStore.isInspector ? '/inspector/rectification/pending' : '/enterprise/rectification/list'
    const [aRes, rRes] = await Promise.allSettled([
      request.get(appealPath, { params: { page: 1, size: 5 } }).catch(() => null),
      request.get(rectPath, { params: { page: 1, size: 5 } }).catch(() => null)
    ])
    if (aRes.value) {
      const d = aRes.value.data?.data || aRes.value.data
      appealList.value = d?.list || d?.content || d?.records || (Array.isArray(d) ? d : [])
    }
    if (rRes.value) {
      const d = rRes.value.data?.data || rRes.value.data
      rectList.value = d?.list || d?.content || d?.records || (Array.isArray(d) ? d : [])
    }
  } catch (e) {}
}

onMounted(loadData)
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: var(--bg-primary);
  padding-bottom: 90px;
}
.page-content {
  max-width: 480px;
  margin: 0 auto;
  padding: 16px;
}

/* Hero区 */
.hero-section {
  margin-bottom: 16px;
}

.welcome-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.greeting {
  font-size: 13px;
  color: var(--text-tertiary);
}

.user-name {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-top: 2px;
}

.avatar {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #fff;
  font-weight: 600;
  flex-shrink: 0;
  box-shadow: var(--shadow-sm);
}

/* 渐变状态卡 */
.status-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 20px;
  border-radius: var(--radius-xl);
  color: #fff;
  box-shadow: var(--shadow-md);
}

.status-card.inspector {
  background: linear-gradient(135deg, #5B7FFF, #7B61FF);
}

.status-card.enterprise {
  background: linear-gradient(135deg, #10B981, #34D399);
}

.status-label {
  display: block;
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 4px;
}

.status-value {
  display: block;
  font-size: 18px;
  font-weight: 700;
}

/* 通知条 */
.notice-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: 12px 16px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all 0.2s;
}

.notice-bar:active {
  transform: scale(0.98);
}

.notice-dot {
  width: 8px;
  height: 8px;
  background: #F59E0B;
  border-radius: 50%;
  flex-shrink: 0;
}

.notice-text {
  flex: 1;
  font-size: 14px;
  color: var(--text-primary);
}

/* 标题 */
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
  margin-top: 4px;
}

/* 功能卡片网格 */
.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.feature-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 18px 14px;
  text-align: center;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all 0.3s ease;
}

.feature-card:active {
  transform: scale(0.96);
}

.feature-card:hover {
  box-shadow: var(--shadow-md);
}

.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.feature-icon svg {
  width: 24px;
  height: 24px;
}

.feature-name {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.feature-desc {
  display: block;
  font-size: 12px;
  color: var(--text-tertiary);
}

/* 列表卡片 */
.list-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 4px 16px;
  box-shadow: var(--shadow-sm);
  margin-bottom: 16px;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #F1F5F9;
  cursor: pointer;
}

.list-item:last-child {
  border-bottom: none;
}

.list-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.list-dot.pending { background: #F59E0B; }
.list-dot.processing { background: #5B7FFF; }
.list-dot.done { background: #10B981; }
.list-dot.rejected { background: #EF4444; }

.list-body {
  flex: 1;
  min-width: 0;
}

.list-title {
  display: block;
  font-size: 14px;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.list-time {
  display: block;
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 2px;
}

.list-status {
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 6px;
  white-space: nowrap;
  flex-shrink: 0;
}

.list-status.pending { background: #FEF3C7; color: #D97706; }
.list-status.processing { background: #E0E7FF; color: #5B7FFF; }
.list-status.done { background: #D1FAE5; color: #059669; }
.list-status.rejected { background: #FEE2E2; color: #DC2626; }

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 0;
  color: #CBD5E1;
  font-size: 13px;
  gap: 8px;
}
</style>
