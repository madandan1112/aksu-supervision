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

      <div v-for="item in list" :key="item.id" class="report-card" @click="showDetail(item)">
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

    <!-- 报告详情弹层 -->
    <div v-if="detailVisible" class="modal-mask" @click.self="detailVisible = false">
      <div class="modal-sheet">
        <div class="modal-header">
          <h3>报告详情</h3>
          <button class="modal-close" @click="detailVisible = false">×</button>
        </div>
        <div class="modal-body">
          <div class="detail-row"><span class="detail-label">报告名称</span><span class="detail-value">{{ detail.title || detail.reportName || '-' }}</span></div>
          <div class="detail-row"><span class="detail-label">报告编号</span><span class="detail-value">{{ detail.reportNo || '-' }}</span></div>
          <div class="detail-row"><span class="detail-label">报告类型</span><span class="detail-value">{{ typeText(detail.reportType) }}</span></div>
          <div class="detail-row"><span class="detail-label">审核状态</span><span class="detail-value">{{ statusText(detail.status) }}</span></div>
          <div class="detail-row"><span class="detail-label">有效期至</span><span class="detail-value">{{ detail.expireDate || '-' }}</span></div>
          <div class="detail-row"><span class="detail-label">提交时间</span><span class="detail-value">{{ detail.createTime || '-' }}</span></div>
          <div class="detail-row" v-if="detail.reviewComment"><span class="detail-label">审核意见</span><span class="detail-value">{{ detail.reviewComment }}</span></div>
          <div class="detail-row" v-if="detail.reviewedBy"><span class="detail-label">审核人</span><span class="detail-value">{{ detail.reviewedBy }}</span></div>
          <div class="detail-row" v-if="detail.fileUrl">
            <span class="detail-label">报告文件</span>
            <a class="detail-link" :href="fileHref(detail.fileUrl)" target="_blank">查看文件</a>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-close" @click="detailVisible = false">关闭</button>
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
const detailVisible = ref(false)
const detail = ref({})

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

// 文件地址：相对路径（对象存储objectName）拼后端网关，完整URL直接用
const fileHref = (url) => {
  if (!url) return '#'
  if (url.startsWith('http')) return url
  return '/api/file/' + url.replace(/^\//, '')
}

const showDetail = (item) => {
  detail.value = item
  detailVisible.value = true
}

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
  cursor: pointer;
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
  background: #DBEAFE;
  color: #2563EB;
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

/* 详情弹层 */
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
  max-height: 80vh;
  overflow-y: auto;
}
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.modal-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}
.modal-close {
  border: none;
  background: #F1F5F9;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  font-size: 16px;
  color: var(--text-secondary);
  cursor: pointer;
}
.detail-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #F8FAFC;
  font-size: 13px;
}
.detail-label {
  color: var(--text-tertiary);
  flex-shrink: 0;
}
.detail-value {
  color: var(--text-primary);
  text-align: right;
  word-break: break-all;
}
.detail-link {
  color: #2563EB;
  text-decoration: none;
}
.modal-footer {
  margin-top: 16px;
}
.btn-close {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: var(--radius-sm);
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  font-size: 15px;
  cursor: pointer;
}
</style>
