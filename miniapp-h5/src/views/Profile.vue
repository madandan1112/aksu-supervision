<template>
  <div class="profile-page">
    <div class="page-content">
      <!-- 顶部用户卡 -->
      <div class="profile-hero">
        <div class="user-card">
          <div class="avatar-large">
            {{ displayName.charAt(0) }}
          </div>
          <div class="user-info">
            <h2>{{ displayName }}</h2>
            <span class="role-badge" :class="userStore.isInspector ? 'inspector' : 'enterprise'">
              {{ userStore.isInspector ? '执法人员' : '企业用户' }}
            </span>
          </div>
        </div>
      </div>

      <!-- 用户信息 -->
      <div class="info-card">
        <div class="card-title">基本信息</div>
        <div class="info-row">
          <div class="info-item">
            <span class="info-label">用户名</span>
            <span class="info-value">{{ userStore.userInfo?.username || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">姓名</span>
            <span class="info-value">{{ userStore.userInfo?.realName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">手机号</span>
            <span class="info-value">{{ userStore.userInfo?.phone || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 企业信息 -->
      <div v-if="userStore.isEnterprise && enterpriseInfo" class="info-card">
        <div class="card-title">企业信息</div>
        <div class="info-row">
          <div class="info-item">
            <span class="info-label">企业名称</span>
            <span class="info-value">{{ enterpriseInfo.name || enterpriseInfo.enterpriseName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">统一信用代码</span>
            <span class="info-value">{{ enterpriseInfo.creditCode || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">法人代表</span>
            <span class="info-value">{{ enterpriseInfo.legalPerson || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="action-card">
        <div class="action-title">快捷操作</div>
        <div class="action-list">
          <div class="action-item" @click="$router.push('/appeal')">
            <div class="action-icon" style="background: linear-gradient(135deg, #E0E7FF, #C7D2FE);">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#5B7FFF" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
            </div>
            <div class="action-body">
              <span class="action-name">{{ userStore.isInspector ? '诉求管理' : '我的诉求' }}</span>
              <span class="action-desc">查看诉求进度</span>
            </div>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
          </div>
          <div class="action-item" @click="$router.push('/rectification')">
            <div class="action-icon" style="background: linear-gradient(135deg, #D1FAE5, #A7F3D0);">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>
            </div>
            <div class="action-body">
              <span class="action-name">{{ userStore.isInspector ? '整改验收' : '整改记录' }}</span>
              <span class="action-desc">查看整改情况</span>
            </div>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
          </div>
          <div class="action-item" @click="$router.push('/report')">
            <div class="action-icon" style="background: linear-gradient(135deg, #FEF3C7, #FDE68A);">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18"/><path d="M9 21V9"/></svg>
            </div>
            <div class="action-body">
              <span class="action-name">{{ userStore.isInspector ? '报告审核' : '合规报告' }}</span>
              <span class="action-desc">查看报告列表</span>
            </div>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
          </div>
        </div>
      </div>

      <button class="btn-logout" @click="handleLogout">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
        退出登录
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import request from '../utils/request'

const router = useRouter()
const userStore = useUserStore()
const enterpriseInfo = ref(null)

const displayName = computed(() => {
  return userStore.userInfo?.realName || userStore.userInfo?.username || '用户'
})

const loadEnterprise = async () => {
  if (userStore.isEnterprise) {
    try {
      const res = await request.get('/enterprise/profile')
      enterpriseInfo.value = res.data?.data || res.data
    } catch (e) {}
  }
}

const handleLogout = () => {
  if (window.confirm('确定退出登录？')) {
    userStore.logout()
    router.push('/')
  }
}

onMounted(loadEnterprise)
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--bg-primary);
  padding-bottom: 90px;
}
.page-content {
  max-width: 480px;
  margin: 0 auto;
  padding: 16px;
}

/* Hero */
.profile-hero {
  margin-bottom: 16px;
}
.user-card {
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  border-radius: var(--radius-xl);
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--shadow-md);
}
.avatar-large {
  width: 60px;
  height: 60px;
  background: rgba(255,255,255,0.2);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: #fff;
  font-weight: 700;
  flex-shrink: 0;
  backdrop-filter: blur(10px);
}
.user-info h2 {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 6px;
}
.role-badge {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: var(--radius-pill);
  background: rgba(255,255,255,0.2);
  color: #fff;
  backdrop-filter: blur(10px);
}

/* 信息卡片 */
.info-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-sm);
}
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}
.info-row {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #F1F5F9;
}
.info-item:last-child {
  border-bottom: none;
}
.info-label {
  font-size: 13px;
  color: var(--text-tertiary);
}
.info-value {
  font-size: 14px;
  color: var(--text-primary);
  text-align: right;
  max-width: 60%;
}

/* 操作卡片 */
.action-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
}
.action-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}
.action-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.action-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s;
}
.action-item:active {
  background: #F8FAFC;
}
.action-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.action-body {
  flex: 1;
}
.action-name {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}
.action-desc {
  display: block;
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 1px;
}

/* 退出按钮 */
.btn-logout {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  height: 48px;
  background: var(--bg-card);
  color: #EF4444;
  border: 1px solid #FEE2E2;
  border-radius: var(--radius-md);
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transition: all 0.2s;
}
.btn-logout:active {
  background: #FEF2F2;
}
</style>
