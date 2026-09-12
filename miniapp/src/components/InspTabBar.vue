<template>
  <view class="insp-tabbar">
    <view
      v-for="tab in tabs"
      :key="tab.path"
      class="insp-tabbar__item"
      :class="{ 'insp-tabbar__item--active': activePath === tab.path }"
      @tap="switchTab(tab.path)"
    >
      <view v-if="tab.center" class="insp-tabbar__center" @tap.stop="switchTab(tab.path)">
        <text class="insp-tabbar__center-text">检</text>
      </view>
      <template v-else>
        <text class="insp-tabbar__icon">{{ tab.icon }}</text>
        <text class="insp-tabbar__label">{{ tab.label }}</text>
      </template>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  active: { type: String, required: true }
})

onMounted(() => {
  // 原生 tabBar 渲染有时序差，重试确保隐藏
  const hide = () => { try { uni.hideTabBar({ animation: false }) } catch (e) { /* ignore */ } }
  hide()
  setTimeout(hide, 200)
  setTimeout(hide, 600)
  // #ifdef H5
  // H5 端 hideTabBar 存在失效场景，直接内联隐藏 + class 兜底
  document.body.classList.add('insp-tabbar-hidden')
  const hideEl = () => {
    const el = document.querySelector('uni-tabbar') || document.querySelector('.uni-tabbar')
    if (el) el.style.display = 'none'
  }
  hideEl()
  setTimeout(hideEl, 200)
  setTimeout(hideEl, 600)
  // #endif
})

const userStore = useUserStore()

const tabs = [
  { path: '/pages/index/index', icon: '⌂', label: '工作台' },
  { path: '/pages/scan/index', icon: '▣', label: '扫码' },
  { path: '/pages/inspection/create', label: '现场检查', center: true },
  { path: '/pages/alerts/list', icon: '⚠', label: '预警' },
  { path: '/pages/inspector/profile', icon: '☺', label: '我的' }
]

const activePath = computed(() => props.active)

function switchTab(path) {
  if (path === activePath.value) return
  uni.reLaunch({ url: path })
}
</script>

<style lang="scss">
/* 全局（非 scoped）：H5 端执法模式隐藏原生 tabBar */
/* #ifdef H5 */
body.insp-tabbar-hidden .uni-tabbar {
  display: none !important;
}
/* #endif */
</style>

<style lang="scss" scoped>
.insp-tabbar {
  position: fixed;
  bottom: 24rpx;
  left: 24rpx;
  right: 24rpx;
  height: 116rpx;
  background-color: rgba(255, 255, 255, 0.97);
  border-radius: 58rpx;
  box-shadow: 0 8rpx 32rpx rgba(24, 24, 27, 0.12);
  display: flex;
  align-items: center;
  z-index: 999;
  padding: 0 12rpx;

  &__item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 4rpx;

    &--active {
      .insp-tabbar__icon,
      .insp-tabbar__label {
        color: #c8102e;
      }
    }
  }

  &__icon {
    font-size: 38rpx;
    color: #a1a1aa;
    line-height: 1;
  }

  &__label {
    font-size: 20rpx;
    color: #a1a1aa;
  }

  &__center {
    width: 100rpx;
    height: 100rpx;
    border-radius: 32rpx;
    margin-top: -68rpx;
    background: linear-gradient(180deg, #d5263d, #b00e24);
    box-shadow: 0 12rpx 28rpx rgba(200, 16, 46, 0.4);
    border: 6rpx solid #ffffff;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__center-text {
    font-size: 36rpx;
    font-weight: 700;
    color: #ffffff;
  }
}
</style>
