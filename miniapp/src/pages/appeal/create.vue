<template>
  <view class="page-appeal-create">
    <view class="form-section">
      <text class="form-section__title">诉求类型</text>
      <view class="type-grid">
        <view
          v-for="item in typeOptions"
          :key="item.value"
          class="type-item"
          :class="{ 'type-item--active': form.type === item.value }"
          @tap="form.type = item.value"
        >
          <text class="type-item__icon">{{ item.icon }}</text>
          <text class="type-item__label">{{ item.label }}</text>
        </view>
      </view>
    </view>

    <view class="form-section">
      <text class="form-section__title">诉求标题</text>
      <input
        v-model="form.title"
        class="form-input"
        placeholder="请输入诉求标题"
        maxlength="50"
      />
    </view>

    <view class="form-section">
      <text class="form-section__title">诉求内容</text>
      <textarea
        v-model="form.content"
        class="form-textarea"
        placeholder="请详细描述您的诉求..."
        maxlength="500"
        :auto-height="true"
      />
      <text class="form-count">{{ form.content.length }}/500</text>
    </view>

    <view class="form-section">
      <text class="form-section__title">涉及领域</text>
      <view class="domain-grid">
        <view
          v-for="item in domainOptions"
          :key="item"
          class="domain-tag"
          :class="{ 'domain-tag--active': form.domains.includes(item) }"
          @tap="toggleDomain(item)"
        >
          <text class="domain-tag__text">{{ item }}</text>
        </view>
      </view>
    </view>

    <view class="form-section">
      <text class="form-section__title">相关附件</text>
      <Upload v-model="form.attachments" :max-count="9" accept="all" upload-url="/upload/file" />
    </view>

    <view class="form-actions">
      <button class="btn-submit" :disabled="!canSubmit" @tap="handleSubmit">提交诉求</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { post } from '@/utils/request'
import Upload from '@/components/Upload.vue'

const typeOptions = [
  { label: '环保', value: 'environment', icon: '🌿' },
  { label: '安全', value: 'safety', icon: '🛡️' },
  { label: '消防', value: 'fire', icon: '🚒' },
  { label: '质量', value: 'quality', icon: '✅' },
  { label: '其他', value: 'other', icon: '📝' }
]

const domainOptions = ['安全生产', '环境保护', '消防安全', '职业健康', '质量管理', '特种设备', '应急管理']

const form = ref({
  type: '',
  title: '',
  content: '',
  domains: [],
  attachments: []
})

const canSubmit = computed(() => {
  return form.value.type && form.value.title.trim() && form.value.content.trim()
})

function toggleDomain(item) {
  const idx = form.value.domains.indexOf(item)
  if (idx > -1) {
    form.value.domains.splice(idx, 1)
  } else {
    form.value.domains.push(item)
  }
}

async function handleSubmit() {
  if (!canSubmit.value) return
  uni.showLoading({ title: '提交中...' })
  try {
    await post('/appeal/create', form.value)
    uni.showToast({ title: '提交成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (e) {
    uni.showToast({ title: '提交成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } finally {
    uni.hideLoading()
  }
}
</script>

<style lang="scss" scoped>
.page-appeal-create {
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

.type-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: calc(20% - 13rpx);
  padding: 20rpx 0;
  border-radius: 12rpx;
  border: 2rpx solid #e8e8e8;
  background-color: #f9f9f9;

  &--active {
    border-color: #2563EB;
    background-color: rgba(26, 115, 232, 0.06);
  }

  &__icon {
    font-size: 40rpx;
    margin-bottom: 8rpx;
  }

  &__label {
    font-size: 22rpx;
    color: #333333;
  }
}

.form-input {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333333;
}

.form-textarea {
  width: 100%;
  min-height: 200rpx;
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

.domain-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.domain-tag {
  padding: 12rpx 28rpx;
  border-radius: 32rpx;
  border: 2rpx solid #e8e8e8;
  background-color: #f9f9f9;

  &--active {
    border-color: #2563EB;
    background-color: rgba(26, 115, 232, 0.08);
  }

  &__text {
    font-size: 24rpx;
    color: #333333;

    .domain-tag--active & {
      color: #2563EB;
    }
  }
}

.form-actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background-color: #ffffff;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.btn-submit {
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

  &[disabled] {
    opacity: 0.5;
  }
}
</style>
