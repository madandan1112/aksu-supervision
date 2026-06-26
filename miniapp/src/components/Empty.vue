<template>
  <view class="empty-state">
    <image
      v-if="image"
      :src="image"
      class="empty-state__image"
      mode="aspectFit"
    />
    <view v-else class="empty-state__icon-wrap">
      <text class="empty-state__icon-text">📭</text>
    </view>
    <text class="empty-state__title">{{ title || '暂无数据' }}</text>
    <text v-if="description" class="empty-state__desc">{{ description }}</text>
    <view v-if="showAction" class="empty-state__action" @tap="$emit('action')">
      <text class="empty-state__action-text">{{ actionText }}</text>
    </view>
  </view>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' },
  description: { type: String, default: '' },
  image: { type: String, default: '' },
  showAction: { type: Boolean, default: false },
  actionText: { type: String, default: '去操作' }
})

defineEmits(['action'])
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 48rpx;

  &__image {
    width: 240rpx;
    height: 240rpx;
    margin-bottom: 32rpx;
  }

  &__icon-wrap {
    width: 200rpx;
    height: 200rpx;
    border-radius: 50%;
    background-color: #f0f1f5;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 32rpx;
  }

  &__icon-text {
    font-size: 80rpx;
  }

  &__title {
    font-size: 30rpx;
    color: #999999;
    margin-bottom: 12rpx;
  }

  &__desc {
    font-size: 26rpx;
    color: #cccccc;
    margin-bottom: 40rpx;
  }

  &__action {
    padding: 16rpx 48rpx;
    background-color: #1a73e8;
    border-radius: 40rpx;
  }

  &__action-text {
    font-size: 28rpx;
    color: #ffffff;
  }
}
</style>
