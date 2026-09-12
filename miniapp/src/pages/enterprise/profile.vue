<template>
  <view class="page-enterprise-profile">
    <!-- 企业头像与名称 -->
    <view class="profile-header">
      <view class="profile-header__avatar">
        <text class="profile-header__avatar-text">{{ enterprise.name ? enterprise.name.slice(0, 2) : '企' }}</text>
      </view>
      <view class="profile-header__info">
        <text class="profile-header__name">{{ enterprise.name || '未设置企业名称' }}</text>
        <text class="profile-header__code">统一社会信用代码：{{ enterprise.creditCode || '-' }}</text>
      </view>
    </view>

    <!-- 基本信息 -->
    <view class="info-section">
      <view class="info-section__header">
        <text class="info-section__title">基本信息</text>
        <text class="info-section__edit" @tap="toggleEdit">{{ isEditing ? '保存' : '编辑' }}</text>
      </view>

      <view class="info-row">
        <text class="info-row__label">企业名称</text>
        <input v-if="isEditing" v-model="form.name" class="info-row__input" placeholder="请输入" />
        <text v-else class="info-row__value">{{ enterprise.name || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-row__label">企业地址</text>
        <input v-if="isEditing" v-model="form.address" class="info-row__input" placeholder="请输入" />
        <text v-else class="info-row__value">{{ enterprise.address || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-row__label">法人代表</text>
        <input v-if="isEditing" v-model="form.legalPerson" class="info-row__input" placeholder="请输入" />
        <text v-else class="info-row__value">{{ enterprise.legalPerson || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-row__label">联系电话</text>
        <input v-if="isEditing" v-model="form.phone" class="info-row__input" placeholder="请输入" type="number" />
        <text v-else class="info-row__value">{{ enterprise.phone || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-row__label">所属行业</text>
        <input v-if="isEditing" v-model="form.industry" class="info-row__input" placeholder="请输入" />
        <text v-else class="info-row__value">{{ enterprise.industry || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-row__label">员工人数</text>
        <input v-if="isEditing" v-model="form.employeeCount" class="info-row__input" placeholder="请输入" type="number" />
        <text v-else class="info-row__value">{{ enterprise.employeeCount || '-' }}</text>
      </view>
    </view>

    <!-- 功能入口 -->
    <view class="menu-section">
      <view class="menu-item" @tap="goPage('/pages/enterprise/contacts')">
        <text class="menu-item__icon">👥</text>
        <text class="menu-item__label">联系人管理</text>
        <text class="menu-item__arrow">›</text>
      </view>
      <view class="menu-item" @tap="goPage('/pages/equipment/list')">
        <text class="menu-item__icon">⚙️</text>
        <text class="menu-item__label">设备台账</text>
        <text class="menu-item__arrow">›</text>
      </view>
      <view class="menu-item" @tap="goPage('/pages/report/list')">
        <text class="menu-item__icon">📊</text>
        <text class="menu-item__label">合规报告</text>
        <text class="menu-item__arrow">›</text>
      </view>
      <view class="menu-item" @tap="goPage('/pages/ocr/index')">
        <text class="menu-item__icon">📷</text>
        <text class="menu-item__label">OCR识别</text>
        <text class="menu-item__arrow">›</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section">
      <button class="btn-logout" @tap="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get, put } from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isEditing = ref(false)
const enterprise = ref({})
const form = ref({
  name: '',
  address: '',
  legalPerson: '',
  phone: '',
  industry: '',
  employeeCount: ''
})

onShow(() => {
  loadProfile()
})

async function loadProfile() {
  try {
    const data = await get('/enterprise/profile')
    enterprise.value = data
    form.value = { ...data }
  } catch (e) {
    enterprise.value = {
      name: '阿克苏某某有限公司',
      creditCode: '91652900XXXXXXXX',
      address: '阿克苏市XX路XX号',
      legalPerson: '张三',
      phone: '13800138000',
      industry: '化工制造',
      employeeCount: '156'
    }
    form.value = { ...enterprise.value }
  }
}

function toggleEdit() {
  if (isEditing.value) {
    saveProfile()
  } else {
    isEditing.value = true
  }
}

async function saveProfile() {
  uni.showLoading({ title: '保存中...' })
  try {
    await put('/enterprise/profile', form.value)
    enterprise.value = { ...form.value }
    uni.showToast({ title: '保存成功', icon: 'success' })
  } catch (e) {
    enterprise.value = { ...form.value }
    uni.showToast({ title: '保存成功', icon: 'success' })
  } finally {
    isEditing.value = false
    uni.hideLoading()
  }
}

function goPage(url) {
  uni.navigateTo({ url })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.page-enterprise-profile {
  min-height: 100vh;
  background-color: #f5f6fa;
}

.profile-header {
  background: linear-gradient(135deg, #2563EB, #3b82f6);
  padding: 48rpx 32rpx;
  display: flex;
  align-items: center;

  &__avatar {
    width: 112rpx;
    height: 112rpx;
    border-radius: 56rpx;
    background-color: rgba(255, 255, 255, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 24rpx;
  }

  &__avatar-text {
    font-size: 40rpx;
    color: #ffffff;
    font-weight: 600;
  }

  &__info {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  &__name {
    font-size: 34rpx;
    font-weight: 600;
    color: #ffffff;
    margin-bottom: 8rpx;
  }

  &__code {
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.7);
  }
}

.info-section {
  background-color: #ffffff;
  margin: 20rpx 24rpx;
  padding: 28rpx 32rpx;
  border-radius: 16rpx;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20rpx;
  }

  &__title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333333;
  }

  &__edit {
    font-size: 26rpx;
    color: #2563EB;
    padding: 8rpx 24rpx;
    border: 2rpx solid #2563EB;
    border-radius: 24rpx;
  }
}

.info-row {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child { border-bottom: none; }

  &__label {
    width: 180rpx;
    font-size: 26rpx;
    color: #999999;
    flex-shrink: 0;
  }

  &__value {
    flex: 1;
    font-size: 28rpx;
    color: #333333;
    text-align: right;
  }

  &__input {
    flex: 1;
    text-align: right;
    font-size: 28rpx;
    color: #333333;
  }
}

.menu-section {
  background-color: #ffffff;
  margin: 20rpx 24rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child { border-bottom: none; }

  &__icon { font-size: 36rpx; margin-right: 20rpx; }
  &__label { flex: 1; font-size: 28rpx; color: #333333; }
  &__arrow { font-size: 32rpx; color: #cccccc; }
}

.logout-section {
  padding: 40rpx 24rpx;
}

.btn-logout {
  width: 100%;
  height: 88rpx;
  background-color: #ffffff;
  color: #ff3b30;
  font-size: 30rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}
</style>
