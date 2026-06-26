<template>
  <view class="page-report-detail">
    <!-- 审核状态 -->
    <view class="status-bar" :class="'status-bar--' + detail.status">
      <text class="status-bar__text">{{ detail.statusText }}</text>
    </view>

    <!-- 报告信息 -->
    <view class="info-card">
      <text class="info-card__title">{{ detail.title }}</text>
      <view class="info-card__meta">
        <StatusTag :text="detail.category" type="primary" size="small" />
        <text class="info-card__time">{{ detail.createTime }}</text>
      </view>
      <view class="info-card__row">
        <text class="info-card__label">报告周期</text>
        <text class="info-card__value">{{ detail.period }}</text>
      </view>
      <view v-if="detail.remark" class="info-card__row">
        <text class="info-card__label">补充说明</text>
        <text class="info-card__value">{{ detail.remark }}</text>
      </view>
    </view>

    <!-- 文件预览 -->
    <view class="section">
      <text class="section__title">报告文件</text>
      <view v-for="(file, idx) in detail.files" :key="idx" class="file-item" @tap="previewFile(file)">
        <text class="file-item__icon">📄</text>
        <view class="file-item__info">
          <text class="file-item__name">{{ file.name || '文件' + (idx + 1) }}</text>
          <text class="file-item__size">{{ file.size || '' }}</text>
        </view>
        <text class="file-item__action">预览</text>
      </view>
    </view>

    <!-- 审核记录 -->
    <view v-if="detail.reviewHistory && detail.reviewHistory.length" class="section">
      <text class="section__title">审核记录</text>
      <StepProgress :steps="detail.reviewHistory" :current="detail.reviewHistory.length - 1" />
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'
import StatusTag from '@/components/StatusTag.vue'
import StepProgress from '@/components/StepProgress.vue'

const detail = ref({})

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const id = page.options?.id || ''
  loadDetail(id)
})

async function loadDetail(id) {
  try {
    const data = await get('/report/detail', { id })
    detail.value = data
  } catch (e) {
    detail.value = {
      id,
      title: 'Q2安全生产合规报告',
      status: 'reviewing',
      statusText: '审核中',
      category: '安全生产',
      createTime: '2026-06-25',
      period: '2026-04-01 至 2026-06-30',
      remark: '本报告涵盖Q2季度的安全生产自查情况',
      files: [
        { name: 'Q2安全生产合规报告.pdf', size: '2.4 MB', url: '' },
        { name: '附件1-安全培训记录.xlsx', size: '580 KB', url: '' }
      ],
      reviewHistory: [
        { title: '已提交', desc: '2026-06-25 10:00' },
        { title: '审核中', desc: '2026-06-26 09:30 · 审核员：李审核' }
      ]
    }
  }
}

function previewFile(file) {
  if (file.url) {
    uni.previewMedia({ sources: [{ url: file.url, type: 'pdf' }] })
  } else {
    uni.showToast({ title: '暂不支持预览', icon: 'none' })
  }
}
</script>

<style lang="scss" scoped>
.page-report-detail {
  min-height: 100vh;
  background-color: #f5f6fa;
}

.status-bar {
  padding: 28rpx 32rpx;
  text-align: center;

  &--pending { background-color: rgba(255, 149, 0, 0.08); }
  &--reviewing { background-color: rgba(26, 115, 232, 0.08); }
  &--approved { background-color: rgba(52, 199, 89, 0.08); }
  &--rejected { background-color: rgba(255, 59, 48, 0.08); }

  &__text {
    font-size: 30rpx;
    font-weight: 600;
    .status-bar--pending & { color: #ff9500; }
    .status-bar--reviewing & { color: #1a73e8; }
    .status-bar--approved & { color: #34c759; }
    .status-bar--rejected & { color: #ff3b30; }
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
    margin-bottom: 16rpx;
    display: block;
  }

  &__meta {
    display: flex;
    align-items: center;
    gap: 16rpx;
    margin-bottom: 20rpx;
  }

  &__time {
    font-size: 22rpx;
    color: #cccccc;
  }

  &__row {
    display: flex;
    padding: 12rpx 0;
    border-top: 1rpx solid #f0f0f0;
  }

  &__label {
    width: 160rpx;
    font-size: 26rpx;
    color: #999999;
    flex-shrink: 0;
  }

  &__value {
    flex: 1;
    font-size: 26rpx;
    color: #333333;
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
}

.file-item {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child { border-bottom: none; }

  &__icon { font-size: 36rpx; margin-right: 16rpx; }

  &__info { flex: 1; }

  &__name {
    font-size: 26rpx;
    color: #333333;
    display: block;
  }

  &__size {
    font-size: 22rpx;
    color: #cccccc;
  }

  &__action {
    font-size: 24rpx;
    color: #1a73e8;
    padding: 8rpx 20rpx;
    border: 2rpx solid #1a73e8;
    border-radius: 20rpx;
  }
}
</style>
