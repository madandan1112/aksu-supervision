<template>
  <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
    <view class="nav-bar__content" :style="{ height: navBarHeight + 'px' }">
      <view v-if="showBack" class="nav-bar__left" @tap="handleBack">
        <text class="nav-bar__icon">&#xe60a;</text>
      </view>
      <view class="nav-bar__title">
        <text class="nav-bar__title-text">{{ title }}</text>
      </view>
      <view class="nav-bar__right">
        <slot name="right"></slot>
      </view>
    </view>
  </view>
  <!-- 占位高度 -->
  <view :style="{ height: (statusBarHeight + navBarHeight) + 'px' }"></view>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  title: { type: String, default: '' },
  showBack: { type: Boolean, default: true },
  background: { type: String, default: '#1A73E8' },
  color: { type: String, default: '#ffffff' }
})

const emit = defineEmits(['back'])

const statusBarHeight = ref(0)
const navBarHeight = ref(44)

const sysInfo = uni.getSystemInfoSync()
statusBarHeight.value = sysInfo.statusBarHeight || 0

function handleBack() {
  emit('back')
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
  } else {
    uni.reLaunch({ url: '/pages/index/index' })
  }
}
</script>

<style lang="scss" scoped>
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background-color: #1a73e8;

  &__content {
    display: flex;
    align-items: center;
    padding: 0 24rpx;
  }

  &__left {
    width: 60rpx;
    height: 60rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__icon {
    font-size: 36rpx;
    color: #ffffff;
  }

  &__title {
    flex: 1;
    text-align: center;
  }

  &__title-text {
    font-size: 34rpx;
    font-weight: 600;
    color: #ffffff;
  }

  &__right {
    min-width: 60rpx;
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
}
</style>
