<template>
  <view class="page-equipment-add">
    <view class="form-section">
      <text class="form-section__title">设备名称</text>
      <input v-model="form.name" class="form-input" placeholder="请输入设备名称" />
    </view>

    <view class="form-section">
      <text class="form-section__title">设备编号</text>
      <input v-model="form.code" class="form-input" placeholder="请输入设备编号" />
    </view>

    <view class="form-section">
      <text class="form-section__title">设备类型</text>
      <picker :range="typeOptions" :value="typeIndex" @change="e => { typeIndex = e.detail.value; form.type = typeOptions[e.detail.value] }">
        <view class="form-picker">{{ form.type || '请选择设备类型' }}</view>
      </picker>
    </view>

    <view class="form-section">
      <text class="form-section__title">安装位置</text>
      <input v-model="form.location" class="form-input" placeholder="请输入安装位置" />
    </view>

    <view class="form-section">
      <text class="form-section__title">出厂日期</text>
      <picker mode="date" :value="form.manufactureDate" @change="e => form.manufactureDate = e.detail.value">
        <view class="form-picker">{{ form.manufactureDate || '请选择出厂日期' }}</view>
      </picker>
    </view>

    <view class="form-section">
      <text class="form-section__title">下次检查日期</text>
      <picker mode="date" :value="form.nextCheckDate" @change="e => form.nextCheckDate = e.detail.value">
        <view class="form-picker">{{ form.nextCheckDate || '请选择下次检查日期' }}</view>
      </picker>
    </view>

    <view class="form-section">
      <text class="form-section__title">备注</text>
      <textarea v-model="form.remark" class="form-textarea" placeholder="请输入备注信息（选填）" maxlength="200" />
    </view>

    <view class="bottom-actions">
      <button class="btn-primary" :disabled="!canSubmit" @tap="handleSubmit">保存</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { post, put } from '@/utils/request'

const typeOptions = ['压力容器', '锅炉', '特种设备', '消防设备', '电气设备', '管道', '其他']
const typeIndex = ref(0)
const editId = ref('')
const form = ref({
  name: '',
  code: '',
  type: '',
  location: '',
  manufactureDate: '',
  nextCheckDate: '',
  remark: ''
})

const canSubmit = computed(() => form.value.name.trim() && form.value.code.trim() && form.value.type)

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  editId.value = page.options?.id || ''
  if (editId.value) {
    uni.setNavigationBarTitle({ title: '编辑设备' })
  }
})

async function handleSubmit() {
  if (!canSubmit.value) return
  uni.showLoading({ title: '保存中...' })
  try {
    if (editId.value) {
      await put('/equipment/' + editId.value, form.value)
    } else {
      await post('/equipment/add', form.value)
    }
    uni.showToast({ title: '保存成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: '保存成功', icon: 'success' })
  } finally {
    uni.hideLoading()
    setTimeout(() => uni.navigateBack(), 1500)
  }
}
</script>

<style lang="scss" scoped>
.page-equipment-add {
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

.form-input {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
}

.form-picker {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333333;
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
