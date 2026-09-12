<template>
  <div class="login-container">
    <!-- 动态背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-orb orb-1"></div>
      <div class="bg-orb orb-2"></div>
      <div class="bg-orb orb-3"></div>
      <div class="bg-orb orb-4"></div>
      <div class="bg-orb orb-5"></div>
      <div class="bg-wave"></div>
    </div>
    
    <!-- 左侧插图区 -->
    <div class="login-left">
      <div class="illustration-content">
        <div class="emblem-tile">
          <MarketLogo :size="76" />
        </div>
        <h1 class="platform-title">阿克苏地区市场监管执法智慧平台</h1>
        <p class="platform-subtitle">智慧监管 · 数据赋能 · 精准治理</p>
        <div class="feature-tags">
          <span class="tag">AI智能预警</span>
          <span class="tag">移动执法</span>
          <span class="tag">企业监管</span>
          <span class="tag">信用公示</span>
        </div>
      </div>
    </div>
    
    <!-- 右侧登录卡片 -->
    <div class="login-right">
      <div class="login-card">
        <div class="login-header">
          <div class="logo-wrapper">
            <MarketLogo :size="48" />
          </div>
          <h2>阿克苏地区市场监管执法智慧平台</h2>
          <p>市场监督管理局综合监管系统</p>
        </div>
        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item prop="captchaCode">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.captchaCode"
                placeholder="请输入验证码"
                prefix-icon="Key"
                @keyup.enter="handleLogin"
              />
              <div class="captcha-img" @click="refreshCaptcha" title="点击刷新验证码">
                <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
                <span v-else class="captcha-loading">加载中...</span>
              </div>
            </div>
          </el-form-item>
          <el-form-item>
            <div class="login-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <!-- 底部版权 -->
    <div class="login-footer">
      <span>由新疆璟达智创科技有限公司开发</span>
      <span class="divider">|</span>
      <span>中国电信云服务技术支持</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { setToken } from '@/utils/auth'
import { login, getCaptcha } from '@/api/auth'
import { ElMessage } from 'element-plus'
import MarketLogo from '@/components/MarketLogo.vue'

const router = useRouter()
const route = useRoute()

const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)
const captchaImage = ref('')
const captchaKey = ref('')

const loginForm = reactive({
  username: '',
  password: '',
  captchaCode: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
  ],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    captchaKey.value = res.data.captchaKey
    captchaImage.value = res.data.captchaImage
  } catch (e) {
    console.error('获取验证码失败', e)
  }
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login({
      username: loginForm.username,
      password: loginForm.password,
      captchaKey: captchaKey.value,
      captchaCode: loginForm.captchaCode
    })
    setToken(res.data.token)
    // 存储用户信息供大屏等页面使用
    localStorage.setItem('aksu_supervision_username', loginForm.username)
    if (res.data.realName) {
      localStorage.setItem('aksu_supervision_realName', res.data.realName)
    }
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/dashboard'
    router.push(redirect)
  } catch (error) {
    // 登录失败刷新验证码
    loginForm.captchaCode = ''
    refreshCaptcha()
    if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('登录失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style lang="scss" scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  background: #F6F7F9;
  position: relative;
  overflow: hidden;
}

/* 动态背景装饰（浅色微装饰） */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.bg-orb { display: none; }
.bg-wave { display: none; }

/* 左侧插图区（市监红 hero） */
.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  z-index: 1;
  background: linear-gradient(160deg, #C8102E 0%, #A50D22 100%);
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    right: -120px;
    top: -120px;
    width: 420px;
    height: 420px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.06);
  }

  &::after {
    content: '';
    position: absolute;
    left: -80px;
    bottom: -100px;
    width: 300px;
    height: 300px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.05);
  }
}

.illustration-content {
  text-align: center;
  color: #fff;
  max-width: 500px;
  position: relative;
  z-index: 1;

  .emblem-tile {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 104px;
    height: 104px;
    border-radius: 26px;
    background: #ffffff;
    box-shadow: 0 16px 40px rgba(0, 0, 0, 0.22);
    margin-bottom: 8px;
  }

  .platform-title {
    font-size: 28px;
    font-weight: 700;
    margin: 24px 0 12px;
    letter-spacing: 2px;
  }

  .platform-subtitle {
    font-size: 15px;
    opacity: 0.92;
    margin-bottom: 28px;
    letter-spacing: 4px;
    color: #F4DCA0;
  }

  .feature-tags {
    display: flex;
    justify-content: center;
    gap: 12px;
    flex-wrap: wrap;

    .tag {
      padding: 6px 16px;
      background: rgba(255, 255, 255, 0.12);
      border: 1px solid rgba(255, 255, 255, 0.28);
      border-radius: 20px;
      font-size: 13px;
      backdrop-filter: blur(10px);
    }
  }
}

/* 右侧登录区 */
.login-right {
  width: 600px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 40px 80px;
  position: relative;
  z-index: 1;
}

.login-card {
  width: 100%;
  padding: 56px 56px;
  background: #ffffff;
  border-radius: 20px;
  border: 1px solid #ececf0;
  box-shadow: 0 12px 40px rgba(24, 24, 27, 0.08);
  position: relative;
  z-index: 1;

  .login-header {
    text-align: center;
    margin-bottom: 28px;

    .logo-wrapper {
      display: flex;
      justify-content: center;
      margin-bottom: 14px;
    }

    h2 {
      margin: 0 0 8px;
      font-size: 20px;
      color: #18181b;
      font-weight: 700;
    }

    p {
      font-size: 13px;
      color: #8a8a9a;
      margin: 0;
    }
  }

  .captcha-row {
    display: flex;
    width: 100%;
    gap: 12px;
    align-items: center;

    .el-input {
      flex: 1;
    }

    .captcha-img {
      flex-shrink: 0;
      width: 120px;
      height: 40px;
      cursor: pointer;
      border: 1px solid #e8ecf1;
      border-radius: 6px;
      overflow: hidden;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #fafafa;
      transition: border-color 0.2s;

      &:hover {
        border-color: #C8102E;
      }

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .captcha-loading {
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .login-options {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .login-btn {
    width: 100%;
    height: 44px;
    font-size: 16px;
    border-radius: 10px;
    background: linear-gradient(135deg, #D5263D, #B00E24);
    border: none;
    box-shadow: 0 6px 16px rgba(200, 16, 46, 0.28);

    &:hover {
      background: linear-gradient(135deg, #C8102E, #A00D25);
      box-shadow: 0 8px 20px rgba(200, 16, 46, 0.35);
    }
  }
}

/* 响应式 */
@media (max-width: 1024px) {
  .login-left {
    display: none;
  }
  .login-right {
    width: 100%;
    padding: 20px 20px 60px;
  }
  .login-card {
    padding: 36px 28px;
  }
}

/* 底部版权 */
.login-footer {
  position: absolute;
  bottom: 24px;
  left: 0;
  width: 100%;
  text-align: center;
  font-size: 13px;
  color: #a1a1aa;
  z-index: 10;
  letter-spacing: 1px;

  .divider {
    margin: 0 10px;
    color: #d4d4d8;
  }
}
</style>
