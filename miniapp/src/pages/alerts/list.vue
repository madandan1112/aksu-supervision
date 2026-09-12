<template>
  <view class="page-alerts">
    <view class="alerts-tabs">
      <view
        v-for="t in tabs"
        :key="t.status"
        class="alerts-tabs__item"
        :class="{ 'alerts-tabs__item--on': current === t.status }"
        @tap="switchTab(t.status)"
      >
        <text class="alerts-tabs__text">{{ t.label }}</text>
      </view>
    </view>

    <view class="alerts-body">
      <view v-if="loading" class="alerts-tip">
        <text class="alerts-tip__text">加载中...</text>
      </view>
      <view v-else-if="list.length === 0" class="alerts-tip">
        <text class="alerts-tip__text">暂无预警记录</text>
      </view>

      <view v-for="a in list" :key="a.id" class="alert-card">
        <view class="alert-card__head">
          <view class="alert-card__level" :class="'alert-card__level--' + (a.alertLevel || 'MEDIUM')">
            <text class="alert-card__level-text">{{ levelText(a.alertLevel) }}</text>
          </view>
          <text class="alert-card__no">#{{ a.id }} · {{ fmtTime(a.createTime) }}</text>
        </view>
        <text class="alert-card__title">{{ a.title }}</text>
        <text v-if="a.content" class="alert-card__content">{{ a.content }}</text>

        <view v-if="a.enterpriseName" class="alert-card__meta-row">
          <text class="alert-card__meta-label">企业</text>
          <text class="alert-card__meta-value">{{ a.enterpriseName }}</text>
        </view>
        <view class="alert-card__meta-row">
          <text class="alert-card__meta-label">状态</text>
          <text class="alert-card__meta-value">{{ statusText(a.status) }}</text>
        </view>

        <view v-if="a.status === 'PENDING' || a.status === 'ESCALATED'" class="alert-card__actions">
          <view class="alert-card__btn alert-card__btn--primary" @tap="acceptAlert(a)">
            <text class="alert-card__btn-text alert-card__btn-text--primary">接收处置</text>
          </view>
          <view class="alert-card__btn" @tap="rejectAlert(a)">
            <text class="alert-card__btn-text">退回</text>
          </view>
        </view>
        <view v-else-if="a.status === 'PROCESSING'" class="alert-card__actions">
          <view class="alert-card__btn alert-card__btn--primary" @tap="handleAlert(a)">
            <text class="alert-card__btn-text alert-card__btn-text--primary">处置完成</text>
          </view>
        </view>
      </view>

      <text class="alerts-foot">升级链：科员 → 科长 → 局领导 → 地区局 · 每小时自动督办</text>
    </view>

    <InspTabBar active="/pages/alerts/list" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get, post } from '@/utils/request'
import InspTabBar from '@/components/InspTabBar.vue'

const tabs = [
  { status: 'PENDING', label: '待处理' },
  { status: 'PROCESSING', label: '处理中' },
  { status: 'HANDLED', label: '已处置' }
]
const current = ref('PENDING')
const list = ref([])
const loading = ref(false)

onShow(() => {
  uni.hideTabBar({ animation: false }).catch?.(() => {})
  loadList()
})

async function loadList() {
  loading.value = true
  try {
    const data = await get('/inspector/alert/list', { page: 1, size: 20, status: current.value })
    const l = data?.list || data?.records || (Array.isArray(data) ? data : [])
    list.value = Array.isArray(l) ? l : []
  } catch (e) {
    list.value = []
  } finally {
    loading.value = false
  }
}

function switchTab(status) {
  if (current.value === status) return
  current.value = status
  loadList()
}

function levelText(level) {
  if (level === 'HIGH') return '高'
  if (level === 'MEDIUM') return '中'
  return '低'
}

function statusText(status) {
  const map = { PENDING: '待处理', PROCESSING: '处理中', HANDLED: '已处置', ESCALATED: '已督办升级', REJECTED: '已退回' }
  return map[status] || status || '-'
}

function fmtTime(t) {
  return t ? String(t).substring(5, 16).replace('T', ' ') : ''
}

async function acceptAlert(a) {
  uni.showLoading({ title: '接收中...' })
  try {
    await post(`/inspector/alert/${a.id}/accept`)
    uni.hideLoading()
    uni.showToast({ title: '已接收', icon: 'success' })
    loadList()
  } catch (e) {
    uni.hideLoading()
    uni.showToast({ title: e.message || '操作失败', icon: 'none' })
  }
}

async function rejectAlert(a) {
  uni.showModal({
    title: '退回预警',
    content: `确定退回预警 #${a.id} 吗？退回后回到待处理池。`,
    success: async (res) => {
      if (!res.confirm) return
      uni.showLoading({ title: '退回中...' })
      try {
        await post(`/inspector/alert/${a.id}/reject`)
        uni.hideLoading()
        uni.showToast({ title: '已退回', icon: 'success' })
        loadList()
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.message || '操作失败', icon: 'none' })
      }
    }
  })
}

async function handleAlert(a) {
  uni.showModal({
    title: '处置完成',
    content: `确认预警 #${a.id} 已处置完成？`,
    success: async (res) => {
      if (!res.confirm) return
      uni.showLoading({ title: '提交中...' })
      try {
        await post(`/inspector/alert/${a.id}/handle`)
        uni.hideLoading()
        uni.showToast({ title: '已处置', icon: 'success' })
        loadList()
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.message || '操作失败', icon: 'none' })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.page-alerts {
  min-height: 100vh;
  background-color: #f6f7f9;
  padding-bottom: 200rpx;
}

.alerts-tabs {
  display: flex;
  background-color: #ffffff;
  padding: 16rpx 24rpx 0;
  gap: 8rpx;

  &__item {
    flex: 1;
    border-radius: 18rpx;
    padding: 16rpx 0;
    display: flex;
    justify-content: center;

    &--on {
      background: linear-gradient(135deg, #d5263d, #b00e24);
      box-shadow: 0 6rpx 18rpx rgba(200, 16, 46, 0.25);
    }
  }

  &__text {
    font-size: 26rpx;
    color: #71717a;

    .alerts-tabs__item--on & {
      color: #ffffff;
      font-weight: 600;
    }
  }
}

.alerts-body {
  padding: 24rpx 28rpx 0;
}

.alerts-tip {
  background-color: #ffffff;
  border-radius: 28rpx;
  padding: 60rpx 0;
  display: flex;
  justify-content: center;

  &__text {
    font-size: 26rpx;
    color: #a1a1aa;
  }
}

.alert-card {
  background-color: #ffffff;
  border: 1rpx solid #ececf0;
  border-radius: 28rpx;
  padding: 28rpx;
  margin-bottom: 22rpx;

  &--pending {
    border-color: #f4cfd6;
  }

  &__head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 14rpx;
  }

  &__level {
    border-radius: 10rpx;
    padding: 4rpx 14rpx;

    &--HIGH {
      background-color: #c8102e;
    }
    &--MEDIUM {
      background-color: #f59e0b;
    }
    &--LOW {
      background-color: #a1a1aa;
    }
  }

  &__level-text {
    font-size: 22rpx;
    font-weight: 700;
    color: #ffffff;
  }

  &__no {
    font-size: 22rpx;
    color: #a1a1aa;
  }

  &__title {
    display: block;
    font-size: 30rpx;
    font-weight: 700;
    color: #18181b;
  }

  &__content {
    display: block;
    font-size: 24rpx;
    color: #71717a;
    line-height: 1.6;
    margin-top: 10rpx;
  }

  &__meta-row {
    display: flex;
    margin-top: 12rpx;
  }

  &__meta-label {
    width: 90rpx;
    font-size: 22rpx;
    color: #a1a1aa;
  }

  &__meta-value {
    flex: 1;
    font-size: 22rpx;
    color: #52525b;
  }

  &__actions {
    display: flex;
    gap: 18rpx;
    margin-top: 22rpx;
  }

  &__btn {
    flex: 1;
    border-radius: 20rpx;
    border: 2rpx solid #e4e4e7;
    background-color: #ffffff;
    padding: 18rpx 0;
    display: flex;
    align-items: center;
    justify-content: center;

    &--primary {
      background: linear-gradient(135deg, #d5263d, #b00e24);
      border: none;
      box-shadow: 0 8rpx 20rpx rgba(200, 16, 46, 0.22);
    }
  }

  &__btn-text {
    font-size: 26rpx;
    font-weight: 600;
    color: #52525b;

    &--primary {
      color: #ffffff;
    }
  }
}

.alerts-foot {
  display: block;
  text-align: center;
  font-size: 22rpx;
  color: #a1a1aa;
  margin-top: 12rpx;
}
</style>
