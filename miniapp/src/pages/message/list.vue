<template>
  <view class="page-message-list">
    <!-- 类型Tab -->
    <view class="tabs">
      <scroll-view scroll-x class="tabs__scroll">
        <view
          v-for="tab in tabs"
          :key="tab.value"
          class="tabs__item"
          :class="{ 'tabs__item--active': currentTab === tab.value }"
          @tap="switchTab(tab.value)"
        >
          <text class="tabs__text">{{ tab.label }}</text>
        </view>
      </scroll-view>
    </view>

    <!-- 消息列表 -->
    <scroll-view scroll-y class="list-scroll" @scrolltolower="loadMore">
      <view v-if="list.length === 0 && !loading">
        <Empty title="暂无消息" />
      </view>
      <view
        v-for="item in list"
        :key="item.id"
        class="msg-card"
        :class="{ 'msg-card--unread': !item.read }"
        @tap="goDetail(item.id)"
      >
        <view class="msg-card__icon-wrap" :class="'msg-card__icon-wrap--' + item.category">
          <text class="msg-card__icon">{{ getCategoryIcon(item.category) }}</text>
        </view>
        <view class="msg-card__content">
          <view class="msg-card__top">
            <text class="msg-card__title">{{ item.title }}</text>
            <text class="msg-card__time">{{ item.time }}</text>
          </view>
          <text class="msg-card__summary">{{ item.summary }}</text>
        </view>
        <view v-if="!item.read" class="msg-card__dot"></view>
      </view>

      <view v-if="loading" class="loading-more">
        <text class="loading-more__text">加载中...</text>
      </view>
      <view v-if="!hasMore && list.length > 0" class="no-more">
        <text class="no-more__text">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get } from '@/utils/request'
import { timeAgo } from '@/utils/format'
import { useMessageStore } from '@/stores/message'
import Empty from '@/components/Empty.vue'

const messageStore = useMessageStore()

const tabs = [
  { label: '全部', value: '' },
  { label: '整改通知', value: 'rectification' },
  { label: '诉求回复', value: 'appeal' },
  { label: '报告审核', value: 'report' },
  { label: '系统通知', value: 'system' }
]

const currentTab = ref('')
const list = ref([])
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)

onShow(() => {
  page.value = 1
  list.value = []
  loadList()
})

function switchTab(val) {
  currentTab.value = val
  page.value = 1
  list.value = []
  hasMore.value = true
  loadList()
}

async function loadList() {
  if (loading.value) return
  loading.value = true
  try {
    const data = await get('/message/list', { category: currentTab.value, page: page.value })
    list.value = page.value === 1 ? (data.list || []) : [...list.value, ...(data.list || [])]
    hasMore.value = (data.list || []).length >= 10
  } catch (e) {
    list.value = [
      { id: 1, title: '整改通知', summary: '您有一条新的消防设施整改通知，请及时处理', time: '2小时前', category: 'rectification', read: false },
      { id: 2, title: '诉求回复', summary: '您提交的环保审批流程咨询已有回复', time: '1天前', category: 'appeal', read: false },
      { id: 3, title: '报告审核通过', summary: 'Q1消防安全检测报告已审核通过', time: '3天前', category: 'report', read: true },
      { id: 4, title: '系统维护通知', summary: '平台将于7月1日02:00-06:00进行系统维护', time: '5天前', category: 'system', read: true }
    ]
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (!hasMore.value || loading.value) return
  page.value++
  loadList()
}

function getCategoryIcon(category) {
  const map = { rectification: '🔧', appeal: '📋', report: '📊', system: '📢' }
  return map[category] || '📩'
}

function goDetail(id) {
  // 标记已读
  const item = list.value.find(m => m.id === id)
  if (item && !item.read) {
    item.read = true
    messageStore.decreaseUnread()
  }
  uni.navigateTo({ url: '/pages/message/detail?id=' + id })
}
</script>

<style lang="scss" scoped>
.page-message-list {
  min-height: 100vh;
  background-color: #f5f6fa;
}

.tabs {
  background-color: #ffffff;
  padding: 0 24rpx;
  margin-bottom: 16rpx;

  &__scroll { white-space: nowrap; }

  &__item {
    display: inline-flex;
    padding: 24rpx 28rpx;
    margin-right: 16rpx;
    position: relative;

    &--active {
      .tabs__text { color: #1a73e8; font-weight: 600; }
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 40rpx;
        height: 6rpx;
        background-color: #1a73e8;
        border-radius: 3rpx;
      }
    }
  }

  &__text { font-size: 28rpx; color: #666666; }
}

.list-scroll {
  height: calc(100vh - 120rpx);
  padding: 0 24rpx;
}

.msg-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 12rpx;
  display: flex;
  align-items: center;

  &--unread { background-color: #fafbff; }

  &__icon-wrap {
    width: 72rpx;
    height: 72rpx;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20rpx;
    flex-shrink: 0;

    &--rectification { background-color: rgba(255, 149, 0, 0.1); }
    &--appeal { background-color: rgba(26, 115, 232, 0.1); }
    &--report { background-color: rgba(52, 199, 89, 0.1); }
    &--system { background-color: rgba(90, 200, 250, 0.1); }
  }

  &__icon { font-size: 36rpx; }

  &__content { flex: 1; }

  &__top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 6rpx;
  }

  &__title { font-size: 28rpx; font-weight: 500; color: #333333; }
  &__time { font-size: 22rpx; color: #cccccc; }
  &__summary {
    font-size: 24rpx;
    color: #999999;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__dot {
    width: 16rpx;
    height: 16rpx;
    background-color: #ff3b30;
    border-radius: 50%;
    margin-left: 12rpx;
    flex-shrink: 0;
  }
}

.loading-more, .no-more {
  text-align: center;
  padding: 32rpx 0;
}
.loading-more__text, .no-more__text {
  font-size: 24rpx;
  color: #cccccc;
}
</style>
