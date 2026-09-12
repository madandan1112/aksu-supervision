<template>
  <view class="step-progress">
    <view
      v-for="(step, index) in steps"
      :key="index"
      class="step-item"
      :class="{ 'step-item--active': index <= current, 'step-item--current': index === current }"
    >
      <!-- 连线 -->
      <view v-if="index > 0" class="step-item__line" :class="{ 'step-item__line--done': index <= current }"></view>
      <!-- 节点 -->
      <view class="step-item__node">
        <view v-if="index < current" class="step-item__dot step-item__dot--done">✓</view>
        <view v-else-if="index === current" class="step-item__dot step-item__dot--current"></view>
        <view v-else class="step-item__dot step-item__dot--wait"></view>
      </view>
      <!-- 文字 -->
      <view class="step-item__info">
        <text class="step-item__title">{{ step.title || step }}</text>
        <text v-if="step.desc" class="step-item__desc">{{ step.desc }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
defineProps({
  steps: { type: Array, default: () => [] },
  current: { type: Number, default: 0 }
})
</script>

<style lang="scss" scoped>
.step-progress {
  padding: 24rpx 0;
}

.step-item {
  display: flex;
  align-items: flex-start;
  position: relative;
  padding-bottom: 40rpx;
  padding-left: 40rpx;

  &:last-child {
    padding-bottom: 0;
  }

  &__line {
    position: absolute;
    left: 15rpx;
    top: 0;
    bottom: 0;
    width: 4rpx;
    background-color: #e8e8e8;

    &--done {
      background-color: #2563EB;
    }
  }

  &__node {
    position: absolute;
    left: 0;
    top: 4rpx;
  }

  &__dot {
    width: 32rpx;
    height: 32rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20rpx;
    color: #ffffff;

    &--done {
      background-color: #2563EB;
    }
    &--current {
      background-color: #2563EB;
      border: 6rpx solid rgba(26, 115, 232, 0.2);
    }
    &--wait {
      background-color: #e8e8e8;
    }
  }

  &__info {
    display: flex;
    flex-direction: column;
  }

  &__title {
    font-size: 28rpx;
    color: #333333;
    line-height: 1.4;
  }

  &__desc {
    font-size: 24rpx;
    color: #999999;
    margin-top: 4rpx;
  }

  &--current {
    .step-item__title {
      color: #2563EB;
      font-weight: 600;
    }
  }

  &--active {
    .step-item__title {
      color: #2563EB;
    }
  }
}
</style>
