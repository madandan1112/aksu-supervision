<template>
  <div class="scan-page">
    <div class="page-content">
      <!-- 顶部返回 -->
      <div class="page-header">
        <button class="back-btn" @click="$router.back()">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <h2>扫码查企</h2>
        <div style="width:36px"></div>
      </div>

      <!-- 搜索区 -->
      <div class="search-card">
        <div class="search-input-wrap">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input v-model="searchCode" type="text" placeholder="输入企业ID或统一信用代码" @keyup.enter="doSearch" />
        </div>
        <button class="btn-search" @click="doSearch">查询</button>
      </div>

      <!-- 结果卡片 -->
      <div v-if="enterprise" class="result-card">
        <div class="card-header">
          <div class="header-title">
            <h3>{{ enterprise.name || enterprise.enterpriseName || '企业信息' }}</h3>
            <span class="tag">企业</span>
          </div>
        </div>
        <div class="info-list">
          <div class="info-row">
            <span class="info-label">统一信用代码</span>
            <span class="info-value">{{ enterprise.creditCode || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">法人代表</span>
            <span class="info-value">{{ enterprise.legalPerson || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">联系电话</span>
            <span class="info-value">{{ enterprise.phone || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">地址</span>
            <span class="info-value">{{ enterprise.address || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">行业</span>
            <span class="info-value">{{ enterprise.industry || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">经营范围</span>
            <span class="info-value">{{ enterprise.businessScope || '-' }}</span>
          </div>
        </div>
      </div>

      <div v-if="searched && !enterprise" class="empty-card">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="1.5"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <span>未查询到相关企业信息</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import request from '../utils/request'

const searchCode = ref('')
const enterprise = ref(null)
const searched = ref(false)

const doSearch = async () => {
  if (!searchCode.value.trim()) {
    window.alert('请输入企业ID或信用代码')
    return
  }
  try {
    const res = await request.get('/enterprise/scan', { params: { code: searchCode.value.trim() } })
    enterprise.value = res.data?.data || res.data || null
    searched.value = true
  } catch (e) {
    const msg = e.response?.data?.message || '查询失败'
    window.alert(msg)
    searched.value = true
    enterprise.value = null
  }
}
</script>

<style scoped>
.scan-page {
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

/* 搜索卡片 */
.search-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 16px;
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
}
.search-input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-md);
  padding: 0 12px;
  height: 44px;
  gap: 8px;
}
.search-input-wrap input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 14px;
  outline: none;
  color: var(--text-primary);
}
.search-input-wrap input::placeholder {
  color: #CBD5E1;
}
.btn-search {
  height: 44px;
  padding: 0 20px;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  flex-shrink: 0;
}
.btn-search:active {
  opacity: 0.9;
}

/* 结果卡片 */
.result-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}
.card-header {
  margin-bottom: 16px;
  padding-bottom: 14px;
  border-bottom: 1px solid #F1F5F9;
}
.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
}
.header-title h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}
.tag {
  font-size: 11px;
  background: #E0E7FF;
  color: #5B7FFF;
  padding: 2px 8px;
  border-radius: 6px;
}
.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.info-label {
  font-size: 13px;
  color: var(--text-tertiary);
  flex-shrink: 0;
}
.info-value {
  font-size: 14px;
  color: var(--text-primary);
  text-align: right;
  word-break: break-all;
  max-width: 60%;
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
