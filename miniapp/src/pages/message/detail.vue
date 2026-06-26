<template>
  <view class="page-message-detail">
    <view class="detail-card">
      <view class="detail-card__header">
        <view class="detail-card__icon-wrap" :class="'detail-card__icon-wrap--' + detail.category">
          <text class="detail-card__icon">{{ getCategoryIcon(detail.category) }}</text>
        </view>
        <view class="detail-card__meta">
          <text class="detail-card__title">{{ detail.title }}</text>
          <text class="detail-card__time">{{ detail.time }}</text>
        </view>
      </view>
      <view class="detail-card__divider"></view>
      <text class="detail-card__content">{{ detail.content }}</text>
    </view>

    <!-- 快捷操作 -->
    <view v-if="detail.actionUrl" class="action-section">
      <button class="btn-action" @tap="goAction">查看详情</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'

const detail = ref({})

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const id = page.options?.id || ''
  loadDetail(id)
})

async function loadDetail(id) {
  try {
    const data = await get('/message/detail', { id })
    detail.value = data
  } catch (e) {
    detail.value = {
      id,
      title: '整改通知',
      category: 'rectification',
      time: '2026-06-25 14:30',
      content: '经现场检查发现以下问题：1. 三楼东侧消防栓无法正常开启；2. 灭火器部分过期未更换；3. 消防通道标识不清晰。请于2026年7月5日前完成整改，并提交整改反馈。如有疑问请联系检查人员。',
      actionUrl: '/pages/rectification/detail?id=1'
    }
  }
}

function getCategoryIcon(category) {
  const map = { rectification: '🔧', appeal: '📋', report: '📊', system: '📢' }
  return map[category] || '📩'
}

function goAction() {
  if (detail.value.actionUrl) {
    uni.navigateTo({ url: detail.value.actionUrl })
  }
}
</script>

<style lang="scss" scoped>
.page-message-detail {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding: 24rpx;
}

.detail-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx 32rpx;

  &__header {
    display: flex;
    align-items: flex-start;
  }

  &__icon-wrap {
    width: 80rpx;
    height: 80rpx;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20rpx;
    flex-shrink: 0;

    &--rectification { background-color: rgba(255, 149, 0, 0.1); }
    &--appeal { background-color: rgba(26, 115, 232, 0.1); }
    &--report { background-color: rgba(52, 199, 89, 0.1); }
    &--system { background-color: rgba(90, 200, 250, 0.1); }
  }

  &__icon { font-size: 40rpx; }

  &__meta { flex: 1; }

  &__title {
    font-size: 34rpx;
    font-weight: 600;
    color: #333333;
    display: block;
    margin-bottom: 8rpx;
  }

  &__time {
    font-size: 24rpx;
    color: #cccccc;
  }

  &__divider {
    height: 2rpx;
    background-color: #f0f0f0;
    margin: 24rpx 0;
  }

  &__content {
    font-size: 30rpx;
    color: #333333;
    line-height: 1.8;
  }
}

.action-section {
  margin-top: 32rpx;
  padding: 0 24rpx;
}

.btn-action {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #1a73e8, #4a90e8);
  color: #ffffff;
  font-size: 30rpx;
  font-weight: 600;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}
</style>
