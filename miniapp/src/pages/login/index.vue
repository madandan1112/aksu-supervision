<template>
  <view class="page-login">
    <view class="login-content">
      <!-- 官方徽章 + 标题 -->
      <view class="login-header">
        <view class="login-header__tile">
          <image class="login-header__emblem" src="/static/emblem.png" mode="aspectFit" />
        </view>
        <text class="login-header__title">阿克苏地区市场监管</text>
        <text class="login-header__title login-header__title--sub">执法智慧平台</text>
        <text class="login-header__subtitle">智慧监管 · 数据赋能 · 精准治理</text>
      </view>

      <!-- 身份选择 -->
      <view v-if="!selectedRole" class="role-section">
        <text class="role-section__title">选择您的身份</text>
        <view class="role-grid">
          <view class="role-card" @tap="selectRole('inspector')">
            <view class="role-card__icon role-card__icon--inspector">
              <text class="role-card__icon-text">执</text>
            </view>
            <text class="role-card__name">执法人员</text>
            <text class="role-card__desc">扫码查企 · 现场检查 · 预警处置</text>
          </view>
          <view class="role-card" @tap="selectRole('enterprise')">
            <view class="role-card__icon role-card__icon--enterprise">
              <text class="role-card__icon-text">企</text>
            </view>
            <text class="role-card__name">企业用户</text>
            <text class="role-card__desc">诉求直达 · 整改反馈 · 合规报告</text>
          </view>
        </view>
        <view class="login-test">
          <text class="login-test__text">测试账号：inspector1 / ent_user1（密码 123456）</text>
        </view>
      </view>

      <!-- 登录表单 -->
      <view v-else class="login-form">
        <view class="form-header">
          <view class="form-header__back" @tap="selectedRole = ''">
            <text class="form-header__back-text">‹</text>
          </view>
          <text class="form-header__title">{{ selectedRole === 'inspector' ? '执法人员登录' : '企业用户登录' }}</text>
          <view class="form-header__placeholder"></view>
        </view>

        <view class="form-item" :class="{ 'form-item--red': selectedRole === 'inspector' }">
          <input
            class="form-item__input"
            v-model="username"
            placeholder="请输入账号"
            placeholder-class="form-item__placeholder"
          />
        </view>
        <view class="form-item" :class="{ 'form-item--red': selectedRole === 'inspector' }">
          <input
            class="form-item__input"
            v-model="password"
            type="password"
            placeholder="请输入密码"
            placeholder-class="form-item__placeholder"
            @confirm="handleAccountLogin"
          />
        </view>

        <button
          class="btn-login"
          :class="{ 'btn-login--red': selectedRole === 'inspector' }"
          :loading="loading"
          @tap="handleAccountLogin"
        >
          登 录
        </button>

        <view class="login-agreement">
          <view class="checkbox" :class="{ 'checkbox--checked': agreed }" @tap="agreed = !agreed">
            <text v-if="agreed" class="checkbox__icon">✓</text>
          </view>
          <text class="login-agreement__text" @tap="agreed = !agreed">
            我已阅读并同意《用户协议》和《隐私政策》
          </text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const agreed = ref(false)
const selectedRole = ref('')
const username = ref('')
const password = ref('')
const loading = ref(false)

function selectRole(role) {
  selectedRole.value = role
  if (role === 'inspector') {
    username.value = 'inspector1'
    password.value = '123456'
  } else {
    username.value = 'ent_user1'
    password.value = '123456'
  }
}

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
</script>

<style lang="scss" scoped>
.page-login {
  min-height: 100vh;
  background-color: #F6F7F9;
  position: relative;
  overflow: hidden;
}

.login-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 140rpx 56rpx 80rpx;
}

.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 72rpx;

  &__tile {
    width: 168rpx;
    height: 168rpx;
    border-radius: 44rpx;
    background-color: #ffffff;
    border: 1rpx solid #f0f0f2;
    box-shadow: 0 16rpx 48rpx rgba(24, 24, 27, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 40rpx;
  }

  &__emblem {
    width: 136rpx;
    height: 136rpx;
  }

  &__title {
    font-size: 40rpx;
    font-weight: 700;
    color: #18181b;
    margin-bottom: 8rpx;
    text-align: center;
    line-height: 1.4;

    &--sub {
      margin-bottom: 16rpx;
    }
  }

  &__subtitle {
    font-size: 26rpx;
    color: #c9a063;
    letter-spacing: 6rpx;
  }
}

/* 身份选择 */
.role-section {
  width: 100%;

  &__title {
    display: block;
    font-size: 30rpx;
    font-weight: 600;
    color: #18181b;
    margin-bottom: 32rpx;
  }
}

.role-grid {
  display: flex;
  gap: 24rpx;
}

.role-card {
  flex: 1;
  background-color: #ffffff;
  border-radius: 36rpx;
  border: 1rpx solid #ececf0;
  box-shadow: 0 8rpx 28rpx rgba(24, 24, 27, 0.06);
  padding: 48rpx 28rpx;
  display: flex;
  flex-direction: column;
  align-items: center;

  &__icon {
    width: 104rpx;
    height: 104rpx;
    border-radius: 28rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 24rpx;

    &--inspector {
      background: linear-gradient(135deg, #fae7ea, #f4cfd6);
    }

    &--enterprise {
      background: linear-gradient(135deg, #dbeafe, #bfdbfe);
    }
  }

  &__icon-text {
    font-size: 44rpx;
    font-weight: 700;

    .role-card__icon--inspector & {
      color: #c8102e;
    }

    .role-card__icon--enterprise & {
      color: #2563eb;
    }
  }

  &__name {
    font-size: 32rpx;
    font-weight: 600;
    color: #18181b;
    margin-bottom: 12rpx;
  }

  &__desc {
    font-size: 22rpx;
    color: #a1a1aa;
    text-align: center;
    line-height: 1.5;
  }
}

.login-test {
  margin-top: 48rpx;
  text-align: center;

  &__text {
    font-size: 22rpx;
    color: #a1a1aa;
  }
}

/* 登录表单 */
.login-form {
  width: 100%;
}

.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 44rpx;

  &__back {
    width: 72rpx;
    height: 72rpx;
    border-radius: 22rpx;
    background-color: #ffffff;
    border: 1rpx solid #ececf0;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__back-text {
    font-size: 44rpx;
    color: #71717a;
    line-height: 1;
  }

  &__title {
    font-size: 34rpx;
    font-weight: 600;
    color: #18181b;
  }

  &__placeholder {
    width: 72rpx;
  }
}

.form-item {
  display: flex;
  align-items: center;
  background-color: #ffffff;
  border-radius: 28rpx;
  border: 3rpx solid #e4e4e7;
  padding: 0 32rpx;
  margin-bottom: 28rpx;
  height: 104rpx;

  &--red {
    border-color: #f4cfd6;
  }

  &__input {
    flex: 1;
    font-size: 30rpx;
    color: #18181b;
    height: 104rpx;
  }

  &__placeholder {
    color: #a1a1aa;
  }
}

.btn-login {
  width: 100%;
  height: 104rpx;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  font-size: 34rpx;
  color: #ffffff;
  font-weight: 600;
  box-shadow: 0 10rpx 28rpx rgba(37, 99, 235, 0.28);
  margin-top: 16rpx;

  &--red {
    background: linear-gradient(135deg, #d5263d, #b00e24);
    box-shadow: 0 10rpx 28rpx rgba(200, 16, 46, 0.28);
  }
}

.login-agreement {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  margin-top: 40rpx;

  &__text {
    font-size: 24rpx;
    color: #a1a1aa;
  }
}

.checkbox {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #d4d4d8;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  &--checked {
    background-color: #c8102e;
    border-color: #c8102e;
  }

  &__icon {
    font-size: 22rpx;
    color: #ffffff;
    line-height: 1;
  }
}
</style>
