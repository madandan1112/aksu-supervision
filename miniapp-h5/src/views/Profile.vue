<template>
  <div class="profile-page">
    <div class="page-content">
      <!-- 页面头部 -->
      <div class="page-header">
        <button class="back-btn" @click="$router.back()">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <h2>个人信息</h2>
        <div style="width:36px"></div>
      </div>

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
        <div class="card-title-row">
          <div class="card-title">企业信息</div>
          <button class="edit-btn" @click="$router.push('/enterprise/edit')">编辑</button>
        </div>
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
          <div class="info-item">
            <span class="info-label">行业</span>
            <span class="info-value">{{ enterpriseInfo.industry || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">所属区域</span>
            <span class="info-value">{{ enterpriseInfo.area || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系电话</span>
            <span class="info-value">{{ enterpriseInfo.phone || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">详细地址</span>
            <span class="info-value">{{ enterpriseInfo.address || '-' }}</span>
          </div>
        </div>

        <!-- 营业执照未上传提示 -->
        <div v-if="!enterpriseInfo.licenseUrl" class="license-warning">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
          营业执照未上传，请完善企业信息
        </div>

        <!-- 证照图片预览 -->
        <div v-if="enterpriseInfo.licenseUrl || enterpriseInfo.storefrontPhoto || enterpriseInfo.interiorPhoto" class="photo-preview-section">
          <div class="photo-preview-title">证照信息</div>
          <div class="photo-preview-grid">
            <div v-if="enterpriseInfo.licenseUrl" class="photo-preview-item" @click="previewImage(enterpriseInfo.licenseUrl)">
              <img :src="enterpriseInfo.licenseUrl" class="photo-preview-img" />
              <span class="photo-preview-label">营业执照</span>
            </div>
            <div v-if="enterpriseInfo.storefrontPhoto" class="photo-preview-item" @click="previewImage(enterpriseInfo.storefrontPhoto)">
              <img :src="enterpriseInfo.storefrontPhoto" class="photo-preview-img" />
              <span class="photo-preview-label">门头照</span>
            </div>
            <div v-if="enterpriseInfo.interiorPhoto" class="photo-preview-item" @click="previewImage(enterpriseInfo.interiorPhoto)">
              <img :src="enterpriseInfo.interiorPhoto" class="photo-preview-img" />
              <span class="photo-preview-label">店内照</span>
            </div>
          </div>
        </div>

        <!-- 资质证书 -->
        <div v-if="profileQualificationList.length" class="photo-preview-section">
          <div class="photo-preview-title">资质证书（{{ profileQualificationList.length }}张）</div>
          <div class="photo-preview-grid">
            <div v-for="(url, idx) in profileQualificationList" :key="idx" class="photo-preview-item" @click="previewImage(url, profileQualificationList, idx)">
              <img :src="url" class="photo-preview-img" />
              <span class="photo-preview-label">证书{{ idx + 1 }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 企业信息未填写提示 -->
      <div v-if="userStore.isEnterprise && !enterpriseInfo" class="info-card">
        <div class="empty-enterprise">
          <p>企业信息尚未完善</p>
          <button class="fill-btn" @click="$router.push('/enterprise/edit')">完善企业信息</button>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="action-card">
        <div class="action-title">快捷操作</div>
        <div class="action-list">
          <div class="action-item" @click="$router.push('/appeal')">
            <div class="action-icon" style="background: linear-gradient(135deg, #DBEAFE, #BFDBFE);">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
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

// 资质证书列表
const profileQualificationList = computed(() => {
  if (!enterpriseInfo.value?.qualificationUrls) return []
  try {
    const parsed = typeof enterpriseInfo.value.qualificationUrls === 'string'
      ? JSON.parse(enterpriseInfo.value.qualificationUrls) : enterpriseInfo.value.qualificationUrls
    return Array.isArray(parsed) ? parsed : []
  } catch { return [] }
})

// 图片预览（使用原生方式，兼容H5）
const previewImage = (url, list, idx) => {
  // H5环境使用简单的图片查看
  if (list && list.length > 1) {
    // 多图预览：打开第一张，用户可左右滑动
    const images = list.join(',')
    window.open(url, '_blank')
  } else {
    window.open(url, '_blank')
  }
}

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

/* Hero */
.profile-hero {
  margin-top: 8px;
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
.card-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.card-title-row .card-title {
  margin-bottom: 0;
}
.edit-btn {
  font-size: 13px;
  color: var(--accent-start);
  background: none;
  border: 1px solid var(--accent-start);
  border-radius: 14px;
  padding: 3px 12px;
  cursor: pointer;
}
.license-warning {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 10px;
  padding: 8px 12px;
  background: #FFFBEB;
  border-radius: 8px;
  font-size: 12px;
  color: #92400E;
}
.empty-enterprise {
  text-align: center;
  padding: 20px 0;
}
.empty-enterprise p {
  font-size: 14px;
  color: var(--text-tertiary);
  margin-bottom: 12px;
}
.fill-btn {
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 8px 24px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
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
  word-break: break-all;
}

/* 证照预览 */
.photo-preview-section {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #F1F5F9;
}
.photo-preview-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}
.photo-preview-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.photo-preview-item {
  width: 80px;
  text-align: center;
  cursor: pointer;
}
.photo-preview-img {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #E2E8F0;
}
.photo-preview-label {
  display: block;
  font-size: 11px;
  color: var(--text-tertiary);
  margin-top: 4px;
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
