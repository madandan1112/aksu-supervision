<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <el-icon :size="40" color="#1A73E8"><Shield /></el-icon>
        <h2>阿克苏监管平台</h2>
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
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { setToken } from '@/utils/auth'
import { login, getCaptcha } from '@/api/auth'
import { ElMessage } from 'element-plus'

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
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #1A73E8 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
    animation: rotate 30s linear infinite;
  }

  @keyframes rotate {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }
}

.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 1;

  .login-header {
    text-align: center;
    margin-bottom: 36px;

    h2 {
      margin: 12px 0 8px;
      font-size: 24px;
      color: #303133;
    }

    p {
      font-size: 14px;
      color: #909399;
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
      border: 1px solid #dcdfe6;
      border-radius: 4px;
      overflow: hidden;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f7fa;
      transition: border-color 0.2s;

      &:hover {
        border-color: #1A73E8;
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
    border-radius: 8px;
  }
}
</style>
