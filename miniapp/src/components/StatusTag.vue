<template>
  <view class="status-tag" :class="['status-tag--' + type, 'status-tag--' + size]">
    <text class="status-tag__text">{{ text }}</text>
  </view>
</template>

<script setup>
defineProps({
  text: { type: String, default: '' },
  type: {
    type: String,
    default: 'default',
    validator: (v) => ['default', 'primary', 'success', 'warning', 'danger', 'info'].includes(v)
  },
  size: {
    type: String,
    default: 'normal',
    validator: (v) => ['small', 'normal'].includes(v)
  }
})
</script>

<style lang="scss" scoped>
.status-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 6rpx;
  padding: 4rpx 16rpx;

  &--small {
    padding: 2rpx 12rpx;
    .status-tag__text { font-size: 20rpx; }
  }

  &--normal {
    padding: 4rpx 16rpx;
    .status-tag__text { font-size: 24rpx; }
  }

  &--default {
    background-color: #f0f1f5;
    .status-tag__text { color: #999999; }
  }
  &--primary {
    background-color: rgba(26, 115, 232, 0.1);
    .status-tag__text { color: #2563EB; }
  }
  &--success {
    background-color: rgba(52, 199, 89, 0.1);
    .status-tag__text { color: #34C759; }
  }
  &--warning {
    background-color: rgba(255, 149, 0, 0.1);
    .status-tag__text { color: #FF9500; }
  }
  &--danger {
    background-color: rgba(255, 59, 48, 0.1);
    .status-tag__text { color: #FF3B30; }
  }
  &--info {
    background-color: rgba(90, 200, 250, 0.1);
    .status-tag__text { color: #5AC8FA; }
  }

  &__text {
    line-height: 1.4;
  }
}
</style>
