<template>
  <div class="login-page">
    <!-- 动态背景 -->
    <div class="bg-decoration">
      <div class="bg-orb orb-1"></div>
      <div class="bg-orb orb-2"></div>
      <div class="bg-orb orb-3"></div>
      <div class="bg-orb orb-4"></div>
      <div class="bg-wave"></div>
    </div>

    <!-- 主卡片容器 -->
    <div class="login-card-wrapper">
      <div class="login-card">
        <!-- 顶部英雄区 -->
        <div class="login-hero">
          <!-- 官方徽章 -->
          <div class="hero-logo">
            <img src="../assets/emblem.png" alt="市场监管徽章" />
          </div>
          <h1 class="hero-title">阿克苏地区市场监管执法智慧平台</h1>
          <p class="hero-subtitle">智慧监管 · 数据赋能 · 精准治理</p>
          <div class="feature-tags">
            <span class="tag">AI智能预警</span>
            <span class="tag">移动执法</span>
            <span class="tag">企业监管</span>
            <span class="tag">信用公示</span>
          </div>
        </div>

        <!-- 角色选择 -->
        <div v-if="!selectedRole" class="role-section">
          <div class="section-title">选择您的身份</div>
          <div class="role-grid">
            <div class="role-card-item role-inspector" @click="selectRole('inspector')">
              <div class="role-icon-bg icon-inspector">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M3 7V5a2 2 0 0 1 2-2h2"/><path d="M17 3h2a2 2 0 0 1 2 2v2"/><path d="M21 17v2a2 2 0 0 1-2 2h-2"/><path d="M7 21H5a2 2 0 0 1-2-2v-2"/><rect x="7" y="7" width="10" height="10" rx="1"/>
                </svg>
              </div>
              <span class="role-name">执法人员</span>
              <span class="role-desc">扫码查企 · 检查管理</span>
            </div>
            <div class="role-card-item role-enterprise" @click="selectRole('enterprise')">
              <div class="role-icon-bg icon-enterprise">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>
                </svg>
              </div>
              <span class="role-name">企业用户</span>
              <span class="role-desc">诉求提交 · 整改反馈</span>
            </div>
          </div>
        </div>

        <!-- 登录表单 -->
        <div v-else class="login-form-section" :class="{ 'insp-form': selectedRole === 'inspector' }">
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
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><path d="M3 7V5a2 2 0 0 1 2-2h2"/><path d="M17 3h2a2 2 0 0 1 2 2v2"/><path d="M21 17v2a2 2 0 0 1-2 2h-2"/><path d="M7 21H5a2 2 0 0 1-2-2v-2"/><rect x="7" y="7" width="10" height="10" rx="1"/></svg>
                inspector1
              </div>
              <div class="test-chip" @click="fillAccount('ent_user1', '123456')">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                ent_user1
              </div>
            </div>
          </div>
        </div>

        <!-- 底部版权 -->
        <div class="card-footer">
          <span>由新疆璟达智创科技有限公司开发</span>
          <span class="divider">|</span>
          <span>中国电信云服务技术支持</span>
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
/* ==================== 基础布局（浅色高对比 V2） ==================== */
.login-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #F6F7F9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
  position: relative;
  overflow: hidden;
}

/* ==================== 动态背景装饰（静态微装饰） ==================== */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.bg-decoration::before {
  content: '';
  position: absolute;
  top: -140px;
  right: -120px;
  width: 380px;
  height: 380px;
  border-radius: 50%;
  background: rgba(200, 16, 46, 0.05);
}

.bg-decoration::after {
  content: '';
  position: absolute;
  bottom: -120px;
  left: -100px;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(37, 99, 235, 0.05);
}

.bg-orb, .bg-wave { display: none; }

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.4;
  animation: floatOrb 8s ease-in-out infinite;
}

.orb-1 {
  width: 300px;
  height: 300px;
  background: rgba(255, 107, 107, 0.3);
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.orb-2 {
  width: 250px;
  height: 250px;
  background: rgba(91, 127, 255, 0.3);
  top: 40%;
  right: -80px;
  animation-delay: 2s;
}

.orb-3 {
  width: 200px;
  height: 200px;
  background: rgba(118, 75, 162, 0.3);
  bottom: 20%;
  left: -60px;
  animation-delay: 4s;
}

.orb-4 {
  width: 180px;
  height: 180px;
  background: rgba(0, 180, 219, 0.3);
  bottom: -50px;
  right: 10%;
  animation-delay: 1s;
}

@keyframes floatOrb {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
}

.bg-wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 120px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1440 320'%3E%3Cpath fill='rgba(255,255,255,0.08)' d='M0,224L48,213.3C96,203,192,181,288,181.3C384,181,480,203,576,224C672,245,768,267,864,261.3C960,256,1056,224,1152,208C1248,192,1344,192,1392,192L1440,192L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z'%3E%3C/path%3E%3C/svg%3E");
  background-size: cover;
  animation: waveMove 10s ease-in-out infinite;
}

@keyframes waveMove {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(-30px); }
}

/* ==================== 卡片容器 ==================== */
.login-card-wrapper {
  width: 100%;
  max-width: 520px;
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-card {
  width: 100%;
  background: #ffffff;
  border-radius: 28px;
  padding: 48px 36px 32px;
  box-shadow: 0 12px 40px rgba(24, 24, 27, 0.08);
  border: 1px solid #ECECF0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ==================== Hero区 ==================== */
.login-hero {
  text-align: center;
  margin-bottom: 40px;
  width: 100%;
}

.hero-logo {
  width: 104px;
  height: 104px;
  margin: 0 auto 24px;
  border-radius: 26px;
  background: #ffffff;
  border: 1px solid #F0F0F2;
  box-shadow: 0 10px 28px rgba(24, 24, 27, 0.10);
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-logo img {
  width: 84px;
  height: 84px;
  object-fit: contain;
}

.hero-title {
  font-size: 24px;
  font-weight: 700;
  color: #18181B;
  margin-bottom: 12px;
  letter-spacing: 1px;
  line-height: 1.3;
}

.hero-subtitle {
  font-size: 14px;
  color: #C9A063;
  margin-bottom: 20px;
  letter-spacing: 3px;
}

.feature-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
}

.feature-tags .tag {
  display: inline-block;
  padding: 7px 18px;
  border-radius: 24px;
  background: #FAE7EA;
  border: 1px solid #F4CFD6;
  color: #C8102E;
  font-size: 13px;
  font-weight: 500;
}

/* ==================== 角色选择 ==================== */
.role-section {
  width: 100%;
  animation: fadeUp 0.4s ease;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  padding-left: 4px;
}

.role-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.role-card-item {
  background: #fff;
  border-radius: 20px;
  padding: 32px 20px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.role-card-item:active {
  transform: scale(0.96);
}

.role-card-item:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.role-card-item.role-inspector:hover { border-color: rgba(200, 16, 46, 0.35); }
.role-card-item.role-enterprise:hover { border-color: rgba(37, 99, 235, 0.35); }

.role-icon-bg {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.role-icon-bg.icon-inspector { background: linear-gradient(135deg, #FAE7EA, #F4CFD6); }
.role-icon-bg.icon-enterprise { background: linear-gradient(135deg, #DBEAFE, #BFDBFE); }

.role-name {
  display: block;
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.role-desc {
  display: block;
  font-size: 13px;
  color: #999;
}

/* ==================== 登录表单 ==================== */
.login-form-section {
  width: 100%;
  animation: fadeUp 0.3s ease;
}

.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
}

.back-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: #F0F2F5;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
  transition: all 0.2s;
}

.back-btn:active {
  background: #E2E8F0;
}

.form-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.input-group {
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #555;
  margin-bottom: 10px;
}

.input-wrap {
  display: flex;
  align-items: center;
  background: #F8FAFC;
  border: 1.5px solid #E2E8F0;
  border-radius: 14px;
  padding: 0 16px;
  height: 56px;
  gap: 12px;
  transition: all 0.2s;
}

.input-wrap:focus-within {
  border-color: #2563EB;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
}

.insp-form .input-wrap:focus-within {
  border-color: #C8102E;
  box-shadow: 0 0 0 4px rgba(200, 16, 46, 0.1);
}

.input-wrap input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 16px;
  outline: none;
  color: #333;
}

.input-wrap input::placeholder {
  color: #A0AEC0;
}

.btn-gradient {
  width: 100%;
  height: 54px;
  background: linear-gradient(135deg, #2563EB, #1D4ED8);
  color: #fff;
  border: none;
  border-radius: 14px;
  font-size: 17px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  box-shadow: 0 4px 20px rgba(37, 99, 235, 0.3);
  transition: all 0.3s;
  letter-spacing: 2px;
}

.insp-form .btn-gradient {
  background: linear-gradient(135deg, #D5263D, #B00E24);
  box-shadow: 0 4px 20px rgba(200, 16, 46, 0.3);
}

.btn-gradient:disabled {
  opacity: 0.7;
}

.btn-gradient:active:not(:disabled) {
  transform: scale(0.98);
  box-shadow: 0 2px 12px rgba(24, 24, 27, 0.15);
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

/* ==================== 测试账号 ==================== */
.test-accounts {
  margin-top: 28px;
}

.test-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}

.test-divider::before,
.test-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #E2E8F0;
}

.test-divider span {
  font-size: 13px;
  color: #999;
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
  border-radius: 24px;
  padding: 7px 16px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.test-chip:active {
  background: #E2E8F0;
  transform: scale(0.96);
}

/* ==================== 底部版权（在卡片内） ==================== */
.card-footer {
  width: 100%;
  text-align: center;
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  font-size: 12px;
  color: #999;
  letter-spacing: 0.5px;
  line-height: 1.6;
}

.card-footer .divider {
  margin: 0 10px;
  color: #ccc;
}

/* ==================== 响应式适配 ==================== */
/* 大屏手机 */
@media (min-width: 414px) {
  .login-card {
    padding: 52px 40px 36px;
  }
  .hero-title {
    font-size: 26px;
  }
  .hero-logo {
    width: 110px;
    height: 110px;
  }
}

/* 小平板 */
@media (min-width: 480px) {
  .login-card-wrapper {
    max-width: 460px;
  }
  .hero-title {
    font-size: 28px;
  }
}

/* 小屏手机优化 */
@media (max-width: 360px) {
  .login-page {
    padding: 16px 12px;
  }
  .login-card {
    padding: 32px 20px 24px;
    border-radius: 20px;
  }
  .hero-logo {
    width: 80px;
    height: 80px;
    margin-bottom: 16px;
  }
  .hero-title {
    font-size: 20px;
  }
  .hero-subtitle {
    font-size: 12px;
    letter-spacing: 2px;
  }
  .feature-tags .tag {
    padding: 5px 12px;
    font-size: 11px;
  }
  .role-card-item {
    padding: 24px 14px;
  }
  .role-icon-bg {
    width: 52px;
    height: 52px;
  }
  .role-name {
    font-size: 15px;
  }
  .input-wrap {
    height: 50px;
  }
  .btn-gradient {
    height: 50px;
    font-size: 16px;
  }
  .card-footer {
    font-size: 11px;
  }
}

/* 超小屏手机 */
@media (max-width: 320px) {
  .hero-title {
    font-size: 18px;
  }
  .hero-subtitle {
    font-size: 11px;
  }
  .role-grid {
    gap: 10px;
  }
  .role-card-item {
    padding: 20px 10px;
  }
  .card-footer {
    font-size: 10px;
  }
  .card-footer .divider {
    margin: 0 6px;
  }
}

/* 横屏模式 */
@media (max-height: 500px) and (orientation: landscape) {
  .login-page {
    padding: 12px;
  }
  .login-card {
    padding: 24px 28px 20px;
  }
  .hero-logo {
    width: 60px;
    height: 60px;
    margin-bottom: 12px;
  }
  .hero-title {
    font-size: 18px;
    margin-bottom: 6px;
  }
  .hero-subtitle {
    margin-bottom: 12px;
  }
  .feature-tags {
    display: none;
  }
  .login-hero {
    margin-bottom: 20px;
  }
}

/* 大字体模式适配 */
@media (prefers-reduced-motion: reduce) {
  .login-page {
    animation: none;
  }
  .bg-orb {
    animation: none;
  }
  .bg-wave {
    animation: none;
  }
}
</style>