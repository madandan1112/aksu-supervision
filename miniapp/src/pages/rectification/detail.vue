<template>
  <view class="page-rect-detail">
    <!-- 状态条 -->
    <view class="status-banner" :class="'status-banner--' + detail.status">
      <text class="status-banner__text">{{ detail.statusText }}</text>
      <text v-if="detail.isUrgent" class="status-banner__urgent">⚠ 紧急</text>
    </view>

    <!-- 基本信息 -->
    <view class="info-card">
      <text class="info-card__title">{{ detail.title }}</text>
      <view class="info-card__row">
        <text class="info-card__label">检查来源</text>
        <text class="info-card__value">{{ detail.source }}</text>
      </view>
      <view class="info-card__row">
        <text class="info-card__label">下发日期</text>
        <text class="info-card__value">{{ detail.createTime }}</text>
      </view>
      <view class="info-card__row">
        <text class="info-card__label">截止日期</text>
        <text class="info-card__value info-card__value--danger">{{ detail.deadline }}</text>
      </view>
    </view>

    <!-- 整改要求 -->
    <view class="section">
      <text class="section__title">整改要求</text>
      <text class="section__content">{{ detail.requirement }}</text>
    </view>

    <!-- 佐证图片 -->
    <view v-if="detail.evidenceImages && detail.evidenceImages.length" class="section">
      <text class="section__title">问题佐证</text>
      <view class="image-grid">
        <image
          v-for="(img, idx) in detail.evidenceImages"
          :key="idx"
          :src="img"
          class="image-grid__item"
          mode="aspectFill"
          @tap="previewImages(detail.evidenceImages, idx)"
        />
      </view>
    </view>

    <!-- 操作按钮 -->
    <view v-if="detail.status === 'pending' || detail.status === 'rectifying'" class="bottom-actions">
      <button class="btn-primary" @tap="goFeedback">提交整改反馈</button>
    </view>
    <view v-if="detail.status === 'verifying'" class="bottom-actions">
      <button class="btn-primary" @tap="goResult">查看验收结果</button>
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
  const id = page.options?.id || page.$page?.options?.id || ''
  loadDetail(id)
})

async function loadDetail(id) {
  try {
    const data = await get('/rectification/detail', { id })
    detail.value = data
  } catch (e) {
    detail.value = {
      id,
      title: '消防设施整改通知',
      status: 'pending',
      statusText: '待整改',
      source: '消防检查',
      createTime: '2026-06-25',
      deadline: '2026-07-05',
      isUrgent: true,
      requirement: '经现场检查发现以下问题：1. 三楼东侧消防栓无法正常开启；2. 灭火器部分过期未更换；3. 消防通道标识不清晰。请于限期内完成整改，并提交整改反馈。',
      evidenceImages: [
        'https://via.placeholder.com/200/ff6b6b/fff?text=图1',
        'https://via.placeholder.com/200/ff6b6b/fff?text=图2',
        'https://via.placeholder.com/200/ff6b6b/fff?text=图3'
      ]
    }
  }
}

function previewImages(urls, index) {
  uni.previewImage({ urls, current: urls[index] })
}

function goFeedback() {
  uni.navigateTo({ url: '/pages/rectification/feedback?id=' + detail.value.id })
}

function goResult() {
  uni.navigateTo({ url: '/pages/rectification/result?id=' + detail.value.id })
}
</script>

<style lang="scss" scoped>
.page-rect-detail {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding-bottom: 160rpx;
}

.status-banner {
  padding: 28rpx 32rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;

  &--pending { background-color: rgba(255, 59, 48, 0.08); }
  &--rectifying { background-color: rgba(255, 149, 0, 0.08); }
  &--verifying { background-color: rgba(26, 115, 232, 0.08); }
  &--passed { background-color: rgba(52, 199, 89, 0.08); }
  &--failed { background-color: rgba(255, 59, 48, 0.08); }

  &__text {
    font-size: 30rpx;
    font-weight: 600;

    .status-banner--pending & { color: #ff3b30; }
    .status-banner--rectifying & { color: #ff9500; }
    .status-banner--verifying & { color: #1a73e8; }
    .status-banner--passed & { color: #34c759; }
    .status-banner--failed & { color: #ff3b30; }
  }

  &__urgent {
    font-size: 26rpx;
    color: #ff3b30;
    font-weight: 600;
  }
}

.info-card {
  background-color: #ffffff;
  margin: 20rpx 24rpx;
  padding: 28rpx 32rpx;
  border-radius: 16rpx;

  &__title {
    font-size: 34rpx;
    font-weight: 600;
    color: #333333;
    margin-bottom: 20rpx;
    display: block;
  }

  &__row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12rpx 0;
  }

  &__label {
    font-size: 26rpx;
    color: #999999;
  }

  &__value {
    font-size: 28rpx;
    color: #333333;

    &--danger { color: #ff3b30; font-weight: 600; }
  }
}

.section {
  background-color: #ffffff;
  margin: 20rpx 24rpx;
  padding: 28rpx 32rpx;
  border-radius: 16rpx;

  &__title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333333;
    margin-bottom: 16rpx;
    display: block;
  }

  &__content {
    font-size: 28rpx;
    color: #666666;
    line-height: 1.8;
  }
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;

  &__item {
    width: 200rpx;
    height: 200rpx;
    border-radius: 12rpx;
  }
}

.bottom-actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background-color: #ffffff;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.btn-primary {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #1a73e8, #4a90e8);
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 600;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}
</style>
