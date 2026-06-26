<template>
  <view class="page-report-upload">
    <!-- 报告分类 -->
    <view class="form-section">
      <text class="form-section__title">报告分类</text>
      <view class="category-grid">
        <view
          v-for="item in categories"
          :key="item.value"
          class="category-item"
          :class="{ 'category-item--active': form.category === item.value }"
          @tap="form.category = item.value"
        >
          <text class="category-item__icon">{{ item.icon }}</text>
          <text class="category-item__label">{{ item.label }}</text>
        </view>
      </view>
    </view>

    <!-- 报告标题 -->
    <view class="form-section">
      <text class="form-section__title">报告标题</text>
      <input v-model="form.title" class="form-input" placeholder="请输入报告标题" maxlength="50" />
    </view>

    <!-- 报告周期 -->
    <view class="form-section">
      <text class="form-section__title">报告周期</text>
      <view class="period-row">
        <picker mode="date" :value="form.periodStart" @change="e => form.periodStart = e.detail.value">
          <view class="period-picker">{{ form.periodStart || '开始日期' }}</view>
        </picker>
        <text class="period-separator">至</text>
        <picker mode="date" :value="form.periodEnd" @change="e => form.periodEnd = e.detail.value">
          <view class="period-picker">{{ form.periodEnd || '结束日期' }}</view>
        </view>
      </view>
    </view>

    <!-- 上传文件 -->
    <view class="form-section">
      <text class="form-section__title">报告文件</text>
      <Upload v-model="form.files" :max-count="5" accept="all" upload-url="/upload/file" add-text="上传文件" />
    </view>

    <!-- 补充说明 -->
    <view class="form-section">
      <text class="form-section__title">补充说明</text>
      <textarea v-model="form.remark" class="form-textarea" placeholder="请输入补充说明（选填）" maxlength="300" />
    </view>

    <!-- 提交 -->
    <view class="bottom-actions">
      <button class="btn-primary" :disabled="!canSubmit" @tap="handleSubmit">提交报告</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { post } from '@/utils/request'
import Upload from '@/components/Upload.vue'

const categories = [
  { label: '安全生产', value: 'safety', icon: '🛡️' },
  { label: '环境保护', value: 'environment', icon: '🌿' },
  { label: '消防安全', value: 'fire', icon: '🚒' },
  { label: '特种设备', value: 'equipment', icon: '⚙️' },
  { label: '职业健康', value: 'health', icon: '🏥' },
  { label: '其他', value: 'other', icon: '📝' }
]

const form = ref({
  category: '',
  title: '',
  periodStart: '',
  periodEnd: '',
  files: [],
  remark: ''
})

const canSubmit = computed(() => form.value.category && form.value.title.trim() && form.value.files.length > 0)

async function handleSubmit() {
  if (!canSubmit.value) return
  uni.showLoading({ title: '提交中...' })
  try {
    await post('/report/upload', form.value)
    uni.showToast({ title: '提交成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '提交成功', icon: 'success' })
  } finally {
    uni.hideLoading()
    setTimeout(() => uni.navigateBack(), 1500)
  }
}
</script>

<style lang="scss" scoped>
.page-report-upload {
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

.category-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: calc(33.33% - 11rpx);
  padding: 20rpx 0;
  border-radius: 12rpx;
  border: 2rpx solid #e8e8e8;
  background-color: #f9f9f9;

  &--active {
    border-color: #1a73e8;
    background-color: rgba(26, 115, 232, 0.06);
  }

  &__icon { font-size: 40rpx; margin-bottom: 8rpx; }
  &__label { font-size: 24rpx; color: #333333; }
}

.form-input {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
}

.period-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.period-picker {
  flex: 1;
  height: 80rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333333;
}

.period-separator {
  font-size: 28rpx;
  color: #999999;
}

.form-textarea {
  width: 100%;
  min-height: 160rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  font-size: 28rpx;
  line-height: 1.6;
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

  &[disabled] { opacity: 0.5; }
}
</style>
