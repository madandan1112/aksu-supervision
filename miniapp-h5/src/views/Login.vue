<template>
  <div class="login-page">
    <div class="login-content">
      <!-- 顶部插画区 -->
      <div class="login-hero">
        <div class="hero-icon">
          <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="8" y="20" width="48" height="36" rx="8" fill="url(#grad1)" opacity="0.2"/>
            <rect x="16" y="28" width="32" height="20" rx="4" fill="url(#grad1)"/>
            <circle cx="32" cy="16" r="10" fill="url(#grad2)" opacity="0.6"/>
            <path d="M32 8L34 14H40L35 18L37 24L32 20L27 24L29 18L24 14H30L32 8Z" fill="url(#grad1)"/>
            <defs>
              <linearGradient id="grad1" x1="8" y1="20" x2="56" y2="56" gradientUnits="userSpaceOnUse">
                <stop stop-color="#5B7FFF"/><stop offset="1" stop-color="#7B61FF"/>
              </linearGradient>
              <linearGradient id="grad2" x1="22" y1="6" x2="42" y2="26" gradientUnits="userSpaceOnUse">
                <stop stop-color="#FFD93D"/><stop offset="1" stop-color="#FF6B6B"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1>阿克苏监管平台</h1>
        <p>智慧执法 · 便捷服务</p>
      </div>

      <!-- 角色选择 -->
      <div v-if="!selectedRole" class="role-section">
        <div class="section-title">选择您的身份</div>
        <div class="role-grid">
          <div class="role-card" @click="selectRole('inspector')">
            <div class="role-icon-bg" style="background: linear-gradient(135deg, #E0E7FF, #C7D2FE);">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#5B7FFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M3 7V5a2 2 0 0 1 2-2h2"/><path d="M17 3h2a2 2 0 0 1 2 2v2"/><path d="M21 17v2a2 2 0 0 1-2 2h-2"/><path d="M7 21H5a2 2 0 0 1-2-2v-2"/><rect x="7" y="7" width="10" height="10" rx="1"/>
              </svg>
            </div>
            <span class="role-name">执法人员</span>
            <span class="role-desc">扫码查企 · 检查管理</span>
          </div>
          <div class="role-card" @click="selectRole('enterprise')">
            <div class="role-icon-bg" style="background: linear-gradient(135deg, #FCE7F3, #FBCFE8);">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#EC4899" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>
              </svg>
            </div>
            <span class="role-name">企业用户</span>
            <span class="role-desc">诉求提交 · 整改反馈</span>
          </div>
        </div>
      </div>

      <!-- 登录表单 -->
      <div v-else class="login-form-card">
        <div class="form-header">
          <button class="back-btn" @click="selectedRole = ''">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
          </button>
          <h2>{{ selectedRole === 'inspector' ? '执法人员登录' : '企业用户登录' }}</h2>
          <div style="width:20px"></div>
        </div>

        <div class="input-group">
          <label>账号</label>
          <div class="input-wrap">
            <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#999" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            <input v-model="username" type="text" placeholder="请输入账号" />
          </div>
        </div>

        <div class="input-group">
          <label>密码</label>
          <div class="input-wrap">
            <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#999" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
            <input v-model="password" type="password" placeholder="请输入密码" @keyup.enter="handleLogin" />
          </div>
        </div>

        <button class="btn-gradient" :disabled="loading" @click="handleLogin">
          <span v-if="!loading">登 录</span>
          <span v-else class="loading-dots">登录中</span>
        </button>

        <div class="test-accounts">
          <div class="test-divider">
            <span>测试账号（密码：123456）</span>
          </div>
          <div class="test-list">
            <div class="test-chip" @click="fillAccount('inspector1', '123456')">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#5B7FFF" stroke-width="2"><path d="M3 7V5a2 2 0 0 1 2-2h2"/><path d="M17 3h2a2 2 0 0 1 2 2v2"/><path d="M21 17v2a2 2 0 0 1-2 2h-2"/><path d="M7 21H5a2 2 0 0 1-2-2v-2"/><rect x="7" y="7" width="10" height="10" rx="1"/></svg>
              inspector1
            </div>
            <div class="test-chip" @click="fillAccount('ent_user1', '123456')">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#EC4899" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
              ent_user1
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const selectedRole = ref('')
const username = ref('')
const password = ref('')
const loading = ref(false)

const selectRole = (role) => {
  selectedRole.value = role
  if (role === 'inspector') {
    username.value = 'inspector1'
    password.value = '123456'
  } else {
    username.value = 'ent_user1'
    password.value = '123456'
  }
}

const fillAccount = (user, pwd) => {
  username.value = user
  password.value = pwd
}

const handleLogin = async () => {
  if (!username.value || !password.value) {
    window.alert('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(username.value, password.value)
    router.push('/home')
  } catch (e) {
    const msg = e.response?.data?.message || '登录失败，请检查账号密码'
    window.alert(msg)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #F0F2F5 0%, #E8ECF4 100%);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 40px 20px;
}

.login-content {
  width: 100%;
  max-width: 400px;
}

/* Hero区 */
.login-hero {
  text-align: center;
  margin-bottom: 32px;
}

.hero-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 16px;
}

.hero-icon svg {
  width: 100%;
  height: 100%;
}

.login-hero h1 {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.login-hero p {
  font-size: 14px;
  color: var(--text-tertiary);
}

/* 角色选择 */
.role-section {
  animation: fadeUp 0.4s ease;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  padding-left: 4px;
}

.role-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.role-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 24px 16px;
  text-align: center;
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
}

.role-card:active {
  transform: scale(0.96);
}

.role-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.role-icon-bg {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-md);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}

.role-name {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.role-desc {
  display: block;
  font-size: 12px;
  color: var(--text-tertiary);
}

/* 登录表单 */
.login-form-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 28px 24px;
  box-shadow: var(--shadow-md);
  animation: fadeUp 0.3s ease;
}

.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.back-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: #F0F2F5;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.back-btn:active {
  background: #E2E8F0;
}

.form-header h2 {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.input-group {
  margin-bottom: 18px;
}

.input-group label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.input-wrap {
  display: flex;
  align-items: center;
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-md);
  padding: 0 14px;
  height: 50px;
  gap: 10px;
  transition: all 0.2s;
}

.input-wrap:focus-within {
  border-color: var(--accent-blue);
  background: #fff;
  box-shadow: 0 0 0 3px rgba(91, 127, 255, 0.1);
}

.input-wrap input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 15px;
  outline: none;
  color: var(--text-primary);
}

.input-wrap input::placeholder {
  color: #CBD5E1;
}

.btn-gradient {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  box-shadow: 0 4px 16px rgba(91, 127, 255, 0.3);
  transition: all 0.3s;
}

.btn-gradient:disabled {
  opacity: 0.7;
}

.btn-gradient:active:not(:disabled) {
  transform: scale(0.98);
  box-shadow: 0 2px 8px rgba(91, 127, 255, 0.2);
}

.loading-dots::after {
  content: '';
  animation: dots 1.5s infinite;
}

@keyframes dots {
  0%, 20% { content: '.'; }
  40% { content: '..'; }
  60%, 100% { content: '...'; }
}

/* 测试账号 */
.test-accounts {
  margin-top: 24px;
}

.test-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.test-divider::before,
.test-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #E2E8F0;
}

.test-divider span {
  font-size: 12px;
  color: var(--text-tertiary);
  white-space: nowrap;
}

.test-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.test-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #F1F5F9;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-pill);
  padding: 6px 14px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.test-chip:active {
  background: #E2E8F0;
  transform: scale(0.96);
}
</style>
