<template>
  <view class="page-rect-result">
    <!-- 验收状态 -->
    <view class="result-banner" :class="detail.passed ? 'result-banner--pass' : 'result-banner--fail'">
      <text class="result-banner__icon">{{ detail.passed ? '✅' : '❌' }}</text>
      <text class="result-banner__text">{{ detail.passed ? '验收通过' : '验收未通过' }}</text>
    </view>

    <!-- 验收意见 -->
    <view class="section">
      <text class="section__title">验收意见</text>
      <text class="section__content">{{ detail.opinion }}</text>
      <view class="section__meta">
        <text class="section__meta-item">验收人：{{ detail.verifier }}</text>
        <text class="section__meta-item">验收时间：{{ detail.verifyTime }}</text>
      </view>
    </view>

    <!-- 对比图 -->
    <view class="section">
      <text class="section__title">整改对比</text>
      <view class="compare-list">
        <view v-for="(item, idx) in detail.comparisons" :key="idx" class="compare-item">
          <view class="compare-item__label">
            <text class="compare-item__label--before">整改前</text>
            <text class="compare-item__label--after">整改后</text>
          </view>
          <view class="compare-item__images">
            <image :src="item.before" class="compare-item__img" mode="aspectFill" @tap="previewImage(item.before)" />
            <text class="compare-item__arrow">→</text>
            <image :src="item.after" class="compare-item__img" mode="aspectFill" @tap="previewImage(item.after)" />
          </view>
          <text v-if="item.desc" class="compare-item__desc">{{ item.desc }}</text>
        </view>
      </view>
    </view>

    <!-- 未通过时的操作 -->
    <view v-if="!detail.passed" class="bottom-actions">
      <button class="btn-primary" @tap="goFeedback">重新提交反馈</button>
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
  loadResult(id)
})

async function loadResult(id) {
  try {
    const data = await get('/rectification/result', { id })
    detail.value = data
  } catch (e) {
    detail.value = {
      passed: true,
      opinion: '整改措施到位，消防设施已恢复正常使用，灭火器均在有效期内，消防通道标识清晰。予以通过。',
      verifier: '王检查员',
      verifyTime: '2026-07-02 15:30',
      comparisons: [
        {
          before: 'https://via.placeholder.com/300/ff6b6b/fff?text=整改前',
          after: 'https://via.placeholder.com/300/34c759/fff?text=整改后',
          desc: '消防栓已修复，可正常开启'
        },
        {
          before: 'https://via.placeholder.com/300/ff6b6b/fff?text=整改前',
          after: 'https://via.placeholder.com/300/34c759/fff?text=整改后',
          desc: '灭火器已更换为有效期内的合格产品'
        }
      ]
    }
  }
}

function previewImage(url) {
  uni.previewImage({ urls: [url], current: url })
}

function goFeedback() {
  uni.navigateTo({ url: '/pages/rectification/feedback?id=' + (detail.value.id || '') })
}
</script>

<style lang="scss" scoped>
.page-rect-result {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding-bottom: 160rpx;
}

.result-banner {
  padding: 48rpx 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;

  &--pass { background-color: rgba(52, 199, 89, 0.08); }
  &--fail { background-color: rgba(255, 59, 48, 0.08); }

  &__icon { font-size: 72rpx; margin-bottom: 16rpx; }

  &__text {
    font-size: 34rpx;
    font-weight: 600;

    .result-banner--pass & { color: #34c759; }
    .result-banner--fail & { color: #ff3b30; }
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
    display: block;
    margin-bottom: 16rpx;
  }

  &__meta {
    display: flex;
    gap: 32rpx;
  }

  &__meta-item {
    font-size: 24rpx;
    color: #999999;
  }
}

.compare-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.compare-item {
  &__label {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12rpx;
  }

  &__label--before {
    font-size: 24rpx;
    color: #ff3b30;
    background-color: rgba(255, 59, 48, 0.08);
    padding: 4rpx 16rpx;
    border-radius: 6rpx;
  }

  &__label--after {
    font-size: 24rpx;
    color: #34c759;
    background-color: rgba(52, 199, 89, 0.08);
    padding: 4rpx 16rpx;
    border-radius: 6rpx;
  }

  &__images {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  &__img {
    width: 280rpx;
    height: 200rpx;
    border-radius: 12rpx;
  }

  &__arrow {
    font-size: 40rpx;
    color: #2563EB;
    font-weight: 600;
  }

  &__desc {
    font-size: 24rpx;
    color: #666666;
    margin-top: 8rpx;
    display: block;
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
  background: linear-gradient(135deg, #2563EB, #3b82f6);
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
