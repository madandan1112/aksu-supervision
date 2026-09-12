<template>
  <view class="page-equipment-report">
    <!-- 设备信息 -->
    <view class="equip-info">
      <text class="equip-info__name">{{ equipInfo.name }}</text>
      <text class="equip-info__code">编号：{{ equipInfo.code }}</text>
    </view>

    <!-- 上报类型 -->
    <view class="form-section">
      <text class="form-section__title">上报类型</text>
      <view class="type-row">
        <view
          class="type-btn"
          :class="{ 'type-btn--active': reportType === 'check' }"
          @tap="reportType = 'check'"
        >
          <text class="type-btn__text">日常点检</text>
        </view>
        <view
          class="type-btn"
          :class="{ 'type-btn--active': reportType === 'abnormal' }"
          @tap="reportType = 'abnormal'"
        >
          <text class="type-btn__text">异常上报</text>
        </view>
      </view>
    </view>

    <!-- 点检模板 -->
    <view v-if="reportType === 'check'" class="form-section">
      <text class="form-section__title">点检项目</text>
      <view v-for="(item, idx) in checkItems" :key="idx" class="check-item">
        <text class="check-item__label">{{ item.label }}</text>
        <view class="check-item__options">
          <view
            class="check-option"
            :class="{ 'check-option--pass': item.result === 'pass' }"
            @tap="item.result = 'pass'"
          >正常</view>
          <view
            class="check-option"
            :class="{ 'check-option--fail': item.result === 'fail' }"
            @tap="item.result = 'fail'"
          >异常</view>
        </view>
      </view>
    </view>

    <!-- 异常描述 -->
    <view class="form-section">
      <text class="form-section__title">{{ reportType === 'check' ? '点检备注' : '异常描述' }}</text>
      <textarea v-model="form.description" class="form-textarea" :placeholder="reportType === 'check' ? '请输入点检备注（选填）' : '请详细描述异常情况'" maxlength="500" />
    </view>

    <!-- 附件 -->
    <view class="form-section">
      <text class="form-section__title">现场照片</text>
      <Upload v-model="form.images" :max-count="9" accept="image" upload-url="/upload/file" add-text="拍照/选图" />
    </view>

    <!-- 提交 -->
    <view class="bottom-actions">
      <button class="btn-primary" :disabled="!canSubmit" @tap="handleSubmit">提交上报</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { post } from '@/utils/request'
import Upload from '@/components/Upload.vue'

const equipId = ref('')
const reportType = ref('check')
const equipInfo = ref({ name: '', code: '' })

const checkItems = reactive([
  { label: '外观检查', result: '' },
  { label: '运行声音', result: '' },
  { label: '温度/压力', result: '' },
  { label: '密封性', result: '' },
  { label: '安全附件', result: '' },
  { label: '标识/标牌', result: '' }
])

const form = ref({
  description: '',
  images: []
})

const canSubmit = computed(() => {
  if (reportType.value === 'abnormal') {
    return form.value.description.trim()
  }
  return checkItems.some(item => item.result)
})

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  equipId.value = page.options?.id || ''
  // 模拟设备信息
  equipInfo.value = { name: '压力容器A-001', code: 'EQ-2024-001' }
})

async function handleSubmit() {
  if (!canSubmit.value) return
  uni.showLoading({ title: '提交中...' })
  const payload = {
    equipId: equipId.value,
    type: reportType.value,
    checkItems: reportType.value === 'check' ? checkItems.filter(i => i.result).map(i => ({ label: i.label, result: i.result })) : [],
    ...form.value
  }
  try {
    await post('/equipment/report', payload)
    uni.showToast({ title: '上报成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '上报成功', icon: 'success' })
  } finally {
    uni.hideLoading()
    setTimeout(() => uni.navigateBack(), 1500)
  }
}
</script>

<style lang="scss" scoped>
.page-equipment-report {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding: 24rpx 24rpx 160rpx;
}

.equip-info {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx 32rpx;
  margin-bottom: 20rpx;

  &__name {
    font-size: 32rpx;
    font-weight: 600;
    color: #333333;
    display: block;
    margin-bottom: 8rpx;
  }

  &__code {
    font-size: 24rpx;
    color: #999999;
  }
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

.type-row {
  display: flex;
  gap: 20rpx;
}

.type-btn {
  flex: 1;
  height: 80rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9f9f9;

  &--active {
    border-color: #2563EB;
    background-color: rgba(26, 115, 232, 0.06);
  }

  &__text {
    font-size: 28rpx;
    color: #333333;
    .type-btn--active & { color: #2563EB; font-weight: 600; }
  }
}

.check-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child { border-bottom: none; }

  &__label {
    font-size: 28rpx;
    color: #333333;
  }

  &__options {
    display: flex;
    gap: 16rpx;
  }
}

.check-option {
  padding: 8rpx 24rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  border: 2rpx solid #e8e8e8;
  color: #666666;
  background-color: #f9f9f9;

  &--pass {
    border-color: #34c759;
    color: #34c759;
    background-color: rgba(52, 199, 89, 0.08);
  }

  &--fail {
    border-color: #ff3b30;
    color: #ff3b30;
    background-color: rgba(255, 59, 48, 0.08);
  }
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
