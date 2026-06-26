<template>
  <view class="page-login">
    <view class="login-bg">
      <view class="login-bg__circle login-bg__circle--1"></view>
      <view class="login-bg__circle login-bg__circle--2"></view>
    </view>

    <view class="login-content">
      <!-- Logo & 标题 -->
      <view class="login-header">
        <view class="login-header__logo">
          <text class="login-header__logo-text">监</text>
        </view>
        <text class="login-header__title">阿克苏监管平台</text>
        <text class="login-header__subtitle">企业端</text>
      </view>

      <!-- 账号密码登录表单 -->
      <view class="login-form">
        <view class="form-item">
          <text class="form-item__icon">👤</text>
          <input
            class="form-item__input"
            v-model="username"
            placeholder="请输入账号"
            placeholder-class="form-item__placeholder"
          />
        </view>
        <view class="form-item">
          <text class="form-item__icon">🔒</text>
          <input
            class="form-item__input"
            v-model="password"
            type="password"
            placeholder="请输入密码"
            placeholder-class="form-item__placeholder"
          />
        </view>
        <button class="btn-login" @tap="handleAccountLogin" :loading="loading">
          登录
        </button>
      </view>

      <!-- 分隔线 -->
      <view class="login-divider">
        <view class="login-divider__line"></view>
        <text class="login-divider__text">其他登录方式</text>
        <view class="login-divider__line"></view>
      </view>

      <!-- 微信登录按钮 -->
      <view class="login-actions">
        <button class="btn-wx-login" @tap="handleWxLogin">
          <text class="btn-wx-login__icon">💬</text>
          <text class="btn-wx-login__text">微信一键登录</text>
        </button>
      </view>

      <!-- 协议 -->
      <view class="login-agreement">
        <view class="login-agreement__check" @tap="agreed = !agreed">
          <view class="checkbox" :class="{ 'checkbox--checked': agreed }">
            <text v-if="agreed" class="checkbox__icon">✓</text>
          </view>
        </view>
        <text class="login-agreement__text">
          我已阅读并同意
          <text class="login-agreement__link" @tap.stop="goAgreement('user')">《用户协议》</text>
          和
          <text class="login-agreement__link" @tap.stop="goAgreement('privacy')">《隐私政策》</text>
        </text>
      </view>

      <!-- 测试账号提示 -->
      <view class="login-test-hint">
        <text class="login-test-hint__text">测试账号: ent_user1 / 123456</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const agreed = ref(false)
const username = ref('')
const password = ref('')
const loading = ref(false)

async function handleAccountLogin() {
  if (!username.value.trim()) {
    uni.showToast({ title: '请输入账号', icon: 'none' })
    return
  }
  if (!password.value.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }
  if (!agreed.value) {
    uni.showToast({ title: '请先同意用户协议和隐私政策', icon: 'none' })
    return
  }
  try {
    loading.value = true
    uni.showLoading({ title: '登录中...' })
    await userStore.accountLogin(username.value.trim(), password.value.trim())
    uni.hideLoading()
    loading.value = false
    uni.reLaunch({ url: '/pages/index/index' })
  } catch (e) {
    uni.hideLoading()
    loading.value = false
    uni.showToast({ title: e.message || '登录失败，请重试', icon: 'none' })
  }
}

async function handleWxLogin() {
  if (!agreed.value) {
    uni.showToast({ title: '请先同意用户协议和隐私政策', icon: 'none' })
    return
  }
  try {
    uni.showLoading({ title: '登录中...' })
    await userStore.wxLogin()
    uni.hideLoading()
    uni.reLaunch({ url: '/pages/index/index' })
  } catch (e) {
    uni.hideLoading()
    uni.showToast({ title: '登录失败，请重试', icon: 'none' })
  }
}

function goAgreement(type) {
  uni.showToast({ title: '协议页面开发中', icon: 'none' })
}
</script>

<style lang="scss" scoped>
.page-login {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a73e8, #4a90e8);
  position: relative;
  overflow: hidden;
}

.login-bg {
  &__circle {
    position: absolute;
    border-radius: 50%;
    background-color: rgba(255, 255, 255, 0.05);

    &--1 {
      width: 600rpx;
      height: 600rpx;
      top: -200rpx;
      right: -200rpx;
    }
    &--2 {
      width: 400rpx;
      height: 400rpx;
      bottom: -100rpx;
      left: -100rpx;
    }
  }
}

.login-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 48rpx 80rpx;
}

.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60rpx;

  &__logo {
    width: 140rpx;
    height: 140rpx;
    border-radius: 32rpx;
    background-color: rgba(255, 255, 255, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 32rpx;
    backdrop-filter: blur(10px);
  }

  &__logo-text {
    font-size: 64rpx;
    color: #ffffff;
    font-weight: 700;
  }

  &__title {
    font-size: 44rpx;
    font-weight: 700;
    color: #ffffff;
    margin-bottom: 12rpx;
  }

  &__subtitle {
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.7);
  }
}

.login-form {
  width: 100%;
  margin-bottom: 40rpx;
}

.form-item {
  display: flex;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 16rpx;
  padding: 0 24rpx;
  margin-bottom: 24rpx;
  height: 96rpx;
  backdrop-filter: blur(10px);
  border: 1rpx solid rgba(255, 255, 255, 0.2);

  &__icon {
    font-size: 36rpx;
    margin-right: 16rpx;
  }

  &__input {
    flex: 1;
    font-size: 30rpx;
    color: #ffffff;
    height: 96rpx;
  }

  &__placeholder {
    color: rgba(255, 255, 255, 0.5);
  }
}

.btn-login {
  width: 100%;
  height: 96rpx;
  background-color: #ffffff;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  font-size: 32rpx;
  color: #1a73e8;
  font-weight: 600;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
  margin-top: 16rpx;
}

.login-divider {
  display: flex;
  align-items: center;
  width: 100%;
  margin: 40rpx 0 32rpx;

  &__line {
    flex: 1;
    height: 1rpx;
    background-color: rgba(255, 255, 255, 0.2);
  }

  &__text {
    font-size: 24rpx;
    color: rgba(255, 255, 255, 0.5);
    padding: 0 24rpx;
  }
}

.login-actions {
  width: 100%;
  margin-bottom: 40rpx;
}

.btn-wx-login {
  width: 100%;
  height: 96rpx;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1rpx solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);

  &__icon {
    font-size: 40rpx;
    margin-right: 12rpx;
  }

  &__text {
    font-size: 32rpx;
    color: #ffffff;
    font-weight: 500;
  }
}

.login-agreement {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  margin-bottom: 32rpx;

  &__check {
    flex-shrink: 0;
    padding-top: 4rpx;
  }

  &__text {
    font-size: 24rpx;
    color: rgba(255, 255, 255, 0.6);
    line-height: 1.6;
  }

  &__link {
    color: #ffffff;
    text-decoration: underline;
  }
}

.login-test-hint {
  margin-top: 16rpx;

  &__text {
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.4);
  }
}

.checkbox {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.4);
  border-radius: 6rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &--checked {
    background-color: #ffffff;
    border-color: #ffffff;
  }

  &__icon {
    font-size: 22rpx;
    color: #1a73e8;
    line-height: 1;
  }
}
</style>
