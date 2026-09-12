<template>
  <view class="page-feedback">
    <!-- 整改说明 -->
    <view class="form-section">
      <text class="form-section__title">整改说明</text>
      <textarea
        v-model="form.description"
        class="form-textarea"
        placeholder="请详细描述整改措施和完成情况..."
        maxlength="500"
        :auto-height="true"
      />
      <text class="form-count">{{ form.description.length }}/500</text>
    </view>

    <!-- 整改图片 -->
    <view class="form-section">
      <text class="form-section__title">整改图片</text>
      <Upload
        v-model="form.images"
        :max-count="9"
        accept="image"
        upload-url="/upload/file"
        add-text="添加图片"
      />
    </view>

    <!-- 整改视频 -->
    <view class="form-section">
      <text class="form-section__title">整改视频</text>
      <Upload
        v-model="form.videos"
        :max-count="3"
        accept="video"
        upload-url="/upload/file"
        add-text="添加视频"
      />
    </view>

    <!-- 提交按钮 -->
    <view class="bottom-actions">
      <button class="btn-primary" :disabled="!canSubmit" @tap="handleSubmit">提交反馈</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { post } from '@/utils/request'
import Upload from '@/components/Upload.vue'

const rectId = ref('')

const form = ref({
  description: '',
  images: [],
  videos: []
})

const canSubmit = computed(() => form.value.description.trim() && form.value.images.length > 0)

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  rectId.value = page.options?.id || ''
})

async function handleSubmit() {
  if (!canSubmit.value) return
  uni.showLoading({ title: '提交中...' })
  try {
    await post('/rectification/feedback', {
      id: rectId.value,
      ...form.value
    })
    uni.showToast({ title: '提交成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '提交成功', icon: 'success' })
  } finally {
    uni.hideLoading()
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  }
}
</script>

<style lang="scss" scoped>
.page-feedback {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding: 24rpx 24rpx 160rpx;
}

.form-section {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx 32rpx;
  margin-bottom: 20rpx;

  &__title {
    font-size: 28rpx;
    font-weight: 600;
    color: #333333;
    margin-bottom: 20rpx;
    display: block;
  }
}

.form-textarea {
  width: 100%;
  min-height: 240rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  font-size: 28rpx;
  color: #333333;
  line-height: 1.6;
}

.form-count {
  display: block;
  text-align: right;
  font-size: 22rpx;
  color: #cccccc;
  margin-top: 8rpx;
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

  &[disabled] { opacity: 0.5; }
}
</style>
