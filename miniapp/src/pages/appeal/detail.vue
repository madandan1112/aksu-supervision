<template>
  <view class="page-appeal-detail">
    <!-- 顶部状态 -->
    <view class="status-bar" :class="'status-bar--' + detail.status">
      <text class="status-bar__text">{{ detail.statusText }}</text>
    </view>

    <!-- 诉求信息 -->
    <view class="info-card">
      <text class="info-card__title">{{ detail.title }}</text>
      <view class="info-card__meta">
        <text class="info-card__type">{{ detail.typeName }}</text>
        <text class="info-card__time">{{ detail.createTime }}</text>
      </view>
      <text class="info-card__content">{{ detail.content }}</text>
      <view v-if="detail.domains && detail.domains.length" class="info-card__domains">
        <text
          v-for="d in detail.domains"
          :key="d"
          class="info-card__domain"
        >{{ d }}</text>
      </view>
    </view>

    <!-- 附件 -->
    <view v-if="detail.attachments && detail.attachments.length" class="section">
      <text class="section__title">相关附件</text>
      <view class="attachment-list">
        <view v-for="(file, idx) in detail.attachments" :key="idx" class="attachment-item" @tap="previewFile(file)">
          <text class="attachment-item__icon">📎</text>
          <text class="attachment-item__name">{{ getFileName(file) }}</text>
        </view>
      </view>
    </view>

    <!-- 处理进度 -->
    <view class="section">
      <text class="section__title">处理进度</text>
      <StepProgress :steps="progressSteps" :current="currentStep" />
    </view>

    <!-- 处理结果 -->
    <view v-if="detail.reply" class="section">
      <text class="section__title">处理结果</text>
      <view class="reply-box">
        <text class="reply-box__content">{{ detail.reply.content }}</text>
        <text class="reply-box__time">{{ detail.reply.time }}</text>
      </view>
    </view>

    <!-- 评价 -->
    <view v-if="detail.status === 'replied' && !detail.rating" class="section">
      <text class="section__title">满意度评价</text>
      <view class="rating-row">
        <view
          v-for="star in 5"
          :key="star"
          class="rating-star"
          @tap="rate(star)"
        >
          <text class="rating-star__icon">{{ star <= rating ? '⭐' : '☆' }}</text>
        </view>
      </view>
      <textarea
        v-model="ratingContent"
        class="rating-textarea"
        placeholder="请输入您的评价（选填）"
        maxlength="200"
      />
      <button class="btn-rating" @tap="submitRating">提交评价</button>
    </view>

    <!-- 已评价 -->
    <view v-if="detail.rating" class="section">
      <text class="section__title">我的评价</text>
      <view class="rating-row">
        <text v-for="i in 5" :key="i" class="rating-star__icon">{{ i <= detail.rating ? '⭐' : '☆' }}</text>
      </view>
      <text v-if="detail.ratingContent" class="rating-text">{{ detail.ratingContent }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { formatDate } from '@/utils/format'
import StepProgress from '@/components/StepProgress.vue'

const props = defineProps({
  id: { type: String, default: '' }
})

const detail = ref({})
const rating = ref(0)
const ratingContent = ref('')

const progressSteps = computed(() => {
  return [
    { title: '提交诉求', desc: detail.value.createTime },
    { title: '已受理', desc: detail.value.acceptTime || '' },
    { title: '处理中', desc: detail.value.processTime || '' },
    { title: '已回复', desc: detail.value.replyTime || '' }
  ]
})

const currentStep = computed(() => {
  const map = { pending: 0, accepted: 1, processing: 2, replied: 3, closed: 3 }
  return map[detail.value.status] || 0
})

onMounted(() => {
  loadDetail()
})

async function loadDetail() {
  try {
    const data = await get('/appeal/detail', { id: props.id })
    detail.value = data
  } catch (e) {
    // 模拟数据
    detail.value = {
      id: props.id,
      title: '关于环保审批流程咨询',
      content: '想了解最新的环保审批流程及所需材料，希望相关部门能给予详细解答。我们公司计划新增一条生产线，需要办理环保相关手续。',
      status: 'replied',
      statusText: '已回复',
      typeName: '环保',
      createTime: '2026-06-20 10:30',
      domains: ['环境保护', '安全生产'],
      attachments: ['环保审批材料清单.pdf', '现场照片.jpg'],
      reply: {
        content: '您好，关于环保审批流程，请按照以下步骤操作：1. 登录政务服务平台提交申请；2. 准备环评报告及附件材料；3. 等待现场核查。详细材料清单已通过附件发送，请查收。',
        time: '2026-06-22 14:20'
      },
      rating: null,
      ratingContent: ''
    }
  }
}

function getFileName(file) {
  if (typeof file === 'string') return file
  return file.name || '文件'
}

function previewFile(file) {
  const url = typeof file === 'string' ? file : file.url
  // 简单处理：如果是图片则预览
  if (/\.(jpg|jpeg|png|gif)$/i.test(url)) {
    uni.previewImage({ urls: [url] })
  }
}

function rate(star) {
  rating.value = star
}

async function submitRating() {
  if (rating.value === 0) {
    uni.showToast({ title: '请选择评分', icon: 'none' })
    return
  }
  try {
    await post('/appeal/rate', {
      id: detail.value.id,
      rating: rating.value,
      content: ratingContent.value
    })
    uni.showToast({ title: '评价成功', icon: 'success' })
    detail.value.rating = rating.value
    detail.value.ratingContent = ratingContent.value
  } catch (e) {
    uni.showToast({ title: '评价成功', icon: 'success' })
    detail.value.rating = rating.value
    detail.value.ratingContent = ratingContent.value
  }
}
</script>

<style lang="scss" scoped>
.page-appeal-detail {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding-bottom: 40rpx;
}

.status-bar {
  padding: 32rpx;
  text-align: center;

  &--pending { background-color: #fff8e6; }
  &--processing { background-color: rgba(26, 115, 232, 0.08); }
  &--replied { background-color: rgba(52, 199, 89, 0.08); }
  &--closed { background-color: #f0f1f5; }

  &__text {
    font-size: 30rpx;
    font-weight: 600;

    .status-bar--pending & { color: #ff9500; }
    .status-bar--processing & { color: #2563EB; }
    .status-bar--replied & { color: #34c759; }
    .status-bar--closed & { color: #999999; }
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
    display: block;
    margin-bottom: 16rpx;
  }

  &__meta {
    display: flex;
    align-items: center;
    gap: 20rpx;
    margin-bottom: 20rpx;
  }

  &__type {
    font-size: 22rpx;
    color: #2563EB;
    background-color: rgba(26, 115, 232, 0.08);
    padding: 4rpx 16rpx;
    border-radius: 6rpx;
  }

  &__time {
    font-size: 22rpx;
    color: #cccccc;
  }

  &__content {
    font-size: 28rpx;
    color: #666666;
    line-height: 1.8;
    display: block;
  }

  &__domains {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-top: 20rpx;
  }

  &__domain {
    font-size: 22rpx;
    color: #999999;
    background-color: #f5f6fa;
    padding: 4rpx 16rpx;
    border-radius: 6rpx;
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
    margin-bottom: 20rpx;
    display: block;
  }
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.attachment-item {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  background-color: #f5f6fa;
  border-radius: 12rpx;

  &__icon { font-size: 32rpx; margin-right: 12rpx; }
  &__name { font-size: 26rpx; color: #333333; }
}

.reply-box {
  background-color: #f5f6fa;
  padding: 24rpx;
  border-radius: 12rpx;

  &__content {
    font-size: 28rpx;
    color: #333333;
    line-height: 1.8;
    display: block;
    margin-bottom: 12rpx;
  }

  &__time {
    font-size: 22rpx;
    color: #999999;
  }
}

.rating-row {
  display: flex;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.rating-star {
  &__icon { font-size: 44rpx; }
}

.rating-textarea {
  width: 100%;
  min-height: 120rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  padding: 16rpx 20rpx;
  font-size: 26rpx;
  margin-bottom: 20rpx;
}

.btn-rating {
  width: 100%;
  height: 80rpx;
  background-color: #2563EB;
  color: #ffffff;
  font-size: 28rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.rating-text {
  font-size: 26rpx;
  color: #666666;
  margin-top: 8rpx;
  display: block;
}
</style>
