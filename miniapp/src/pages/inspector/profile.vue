<template>
  <view class="page-profile">
    <!-- 红色头部 -->
    <view class="profile-hero" :style="{ paddingTop: (statusBarHeight + 20) + 'px' }">
      <view class="profile-hero__avatar">
        <text class="profile-hero__avatar-text">{{ avatarChar }}</text>
      </view>
      <view class="profile-hero__info">
        <view class="profile-hero__name-row">
          <text class="profile-hero__name">{{ userStore.userName || '执法员' }}</text>
          <view class="profile-hero__badge">
            <text class="profile-hero__badge-text">执法人员</text>
          </view>
        </view>
        <text class="profile-hero__meta">{{ userStore.userInfo?.orgName || '阿克苏市市监局' }}</text>
      </view>
    </view>

    <view class="profile-body">
      <!-- 本月业绩 -->
      <view class="card">
        <view class="card__title-row">
          <text class="card__title">本月执法业绩</text>
          <text class="card__hint">{{ monthLabel }}</text>
        </view>
        <view class="stats">
          <view class="stats__item">
            <text class="stats__num">{{ stats.inspections }}</text>
            <text class="stats__label">现场检查</text>
          </view>
          <view class="stats__item">
            <text class="stats__num">{{ stats.handled }}</text>
            <text class="stats__label">预警处置</text>
          </view>
          <view class="stats__item">
            <text class="stats__num">100%</text>
            <text class="stats__label">按时率</text>
          </view>
        </view>
      </view>

      <!-- 功能列表 -->
      <view class="card card--list">
        <view class="cell" @tap="goPage('/pages/alerts/list')">
          <text class="cell__icon cell__icon--red">⚠</text>
          <text class="cell__label">预警处置</text>
          <text class="cell__arrow">›</text>
        </view>
        <view class="cell" @tap="goPage('/pages/inspection/create')">
          <text class="cell__icon cell__icon--blue">检</text>
          <text class="cell__label">发起现场检查</text>
          <text class="cell__arrow">›</text>
        </view>
        <view class="cell" @tap="goPage('/pages/scan/index')">
          <text class="cell__icon cell__icon--amber">扫</text>
          <text class="cell__label">扫码查企</text>
          <text class="cell__arrow">›</text>
        </view>
        <view class="cell" @tap="goPage('/pages/message/list')">
          <text class="cell__icon cell__icon--gray">息</text>
          <text class="cell__label">消息中心</text>
          <text class="cell__arrow">›</text>
        </view>
      </view>

      <view class="card card--list">
        <view class="cell cell--danger" @tap="handleLogout">
          <text class="cell__label cell__label--danger">退出登录</text>
        </view>
      </view>

      <text class="profile-foot">阿克苏地区市场监督管理局 · 执法端 v1.0.0</text>
    </view>

    <InspTabBar active="/pages/inspector/profile" />
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { get } from '@/utils/request'
import InspTabBar from '@/components/InspTabBar.vue'

const userStore = useUserStore()
const statusBarHeight = ref(0)
const stats = ref({ inspections: 0, handled: 0 })

const sysInfo = uni.getSystemInfoSync()
statusBarHeight.value = sysInfo.statusBarHeight || 0

const avatarChar = computed(() => (userStore.userName || '执').charAt(0))
const monthLabel = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
})

onShow(() => {
  uni.hideTabBar({ animation: false }).catch?.(() => {})
  loadStats()
})

async function loadStats() {
  try {
    const data = await get('/inspector/alert/statistics')
    stats.value = {
      inspections: data?.monthInspections ?? data?.monthCount ?? 0,
      handled: data?.handled ?? data?.HANDLED ?? 0
    }
  } catch (e) { /* 静默 */ }
}

function goPage(url) {
  uni.reLaunch({ url })
}

function handleLogout() {
  uni.showModal({
    title: '退出登录',
    content: '确定退出当前账号吗？',
    success: (res) => {
      if (res.confirm) userStore.logout()
    }
  })
}
</script>

<style lang="scss" scoped>
.page-profile {
  min-height: 100vh;
  background-color: #f6f7f9;
  padding-bottom: 200rpx;
}

.profile-hero {
  background: linear-gradient(165deg, #c8102e 0%, #a50d22 100%);
  padding: 20rpx 32rpx 44rpx;
  border-radius: 0 0 44rpx 44rpx;
  display: flex;
  align-items: center;
  gap: 26rpx;

  &__avatar {
    width: 116rpx;
    height: 116rpx;
    border-radius: 34rpx;
    background-color: rgba(255, 255, 255, 0.95);
    border: 4rpx solid rgba(229, 200, 147, 0.7);
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__avatar-text {
    font-size: 48rpx;
    font-weight: 800;
    color: #b00e24;
  }

  &__info {
    flex: 1;
  }

  &__name-row {
    display: flex;
    align-items: center;
    gap: 14rpx;
  }

  &__name {
    font-size: 38rpx;
    font-weight: 700;
    color: #ffffff;
  }

  &__badge {
    background-color: rgba(229, 200, 147, 0.2);
    border: 1rpx solid rgba(229, 200, 147, 0.5);
    border-radius: 10rpx;
    padding: 4rpx 14rpx;
  }

  &__badge-text {
    font-size: 20rpx;
    color: #f4dca0;
  }

  &__meta {
    display: block;
    font-size: 24rpx;
    color: rgba(255, 255, 255, 0.8);
    margin-top: 10rpx;
  }
}

.profile-body {
  padding: 28rpx;
}

.card {
  background-color: #ffffff;
  border-radius: 28rpx;
  border: 1rpx solid #ececf0;
  padding: 28rpx;
  margin-bottom: 24rpx;

  &__title-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 22rpx;
  }

  &__title {
    font-size: 28rpx;
    font-weight: 700;
    color: #18181b;
  }

  &__hint {
    font-size: 22rpx;
    color: #a1a1aa;
  }
}

.stats {
  display: flex;

  &__item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  &__item + &__item {
    border-left: 1rpx solid #f4f4f5;
  }

  &__num {
    font-size: 44rpx;
    font-weight: 800;
    color: #18181b;
  }

  &__label {
    font-size: 22rpx;
    color: #a1a1aa;
    margin-top: 8rpx;
  }
}

.cell {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 26rpx 8rpx;

  & + & {
    border-top: 1rpx solid #f4f4f5;
  }

  &__icon {
    width: 60rpx;
    height: 60rpx;
    border-radius: 18rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 26rpx;
    font-weight: 700;

    &--red {
      background-color: #fae7ea;
      color: #c8102e;
    }
    &--blue {
      background-color: #dbeafe;
      color: #2563eb;
    }
    &--amber {
      background-color: #fef3c7;
      color: #d97706;
    }
    &--gray {
      background-color: #f4f4f5;
      color: #71717a;
    }
  }

  &__label {
    flex: 1;
    font-size: 28rpx;
    color: #18181b;

    &--danger {
      color: #c8102e;
      text-align: center;
    }
  }

  &__arrow {
    font-size: 32rpx;
    color: #d4d4d8;
  }

  &--danger {
    justify-content: center;
  }
}

.profile-foot {
  display: block;
  text-align: center;
  font-size: 22rpx;
  color: #a1a1aa;
  margin-top: 8rpx;
}
</style>
