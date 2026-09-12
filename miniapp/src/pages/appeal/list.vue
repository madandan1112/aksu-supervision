<template>
  <view class="page-appeal-list">
    <!-- 状态筛选Tab -->
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

    <!-- 诉求列表 -->
    <scroll-view
      scroll-y
      class="list-scroll"
      @scrolltolower="loadMore"
    >
      <view v-if="list.length === 0 && !loading" class="list-empty">
        <Empty title="暂无诉求记录" description="点击下方按钮新建诉求" :show-action="true" action-text="新建诉求" @action="goCreate" />
      </view>
      <view
        v-for="item in list"
        :key="item.id"
        class="appeal-card"
        @tap="goDetail(item.id)"
      >
        <view class="appeal-card__header">
          <text class="appeal-card__title">{{ item.title }}</text>
          <StatusTag :text="item.statusText" :type="getStatusType(item.status)" />
        </view>
        <text class="appeal-card__content">{{ item.content }}</text>
        <view class="appeal-card__footer">
          <text class="appeal-card__type">{{ item.typeName }}</text>
          <text class="appeal-card__time">{{ item.createTime }}</text>
        </view>
      </view>
      <view v-if="loading" class="loading-more">
        <text class="loading-more__text">加载中...</text>
      </view>
      <view v-if="!hasMore && list.length > 0" class="no-more">
        <text class="no-more__text">没有更多了</text>
      </view>
    </scroll-view>

    <!-- 新建诉求按钮 -->
    <view class="fab" @tap="goCreate">
      <text class="fab__icon">+</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get } from '@/utils/request'
import { formatDate } from '@/utils/format'
import StatusTag from '@/components/StatusTag.vue'
import Empty from '@/components/Empty.vue'

const tabs = [
  { label: '全部', value: '' },
  { label: '待处理', value: 'pending' },
  { label: '处理中', value: 'processing' },
  { label: '已回复', value: 'replied' },
  { label: '已关闭', value: 'closed' }
]

const currentTab = ref('')
const list = ref([])
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)
const pageSize = 10

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
    const data = await get('/appeal/list', {
      status: currentTab.value,
      page: page.value,
      pageSize
    })
    const items = (data.list || []).map(item => ({
      ...item,
      createTime: formatDate(item.createTime),
      statusText: getStatusText(item.status)
    }))
    if (page.value === 1) {
      list.value = items
    } else {
      list.value = [...list.value, ...items]
    }
    hasMore.value = items.length >= pageSize
  } catch (e) {
    // 使用模拟数据
    list.value = [
      { id: 1, title: '关于环保审批流程咨询', content: '想了解最新的环保审批流程及所需材料', status: 'pending', statusText: '待处理', typeName: '环保', createTime: '2026-06-20' },
      { id: 2, title: '安全生产许可证到期续期', content: '安全生产许可证将于下月到期，请问续期流程', status: 'processing', statusText: '处理中', typeName: '安全', createTime: '2026-06-18' },
      { id: 3, title: '消防验收相关问题', content: '新建厂房消防验收需要准备哪些材料', status: 'replied', statusText: '已回复', typeName: '消防', createTime: '2026-06-15' }
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

function getStatusText(status) {
  const map = { pending: '待处理', processing: '处理中', replied: '已回复', closed: '已关闭' }
  return map[status] || status
}

function getStatusType(status) {
  const map = { pending: 'warning', processing: 'primary', replied: 'success', closed: 'default' }
  return map[status] || 'default'
}

function goCreate() {
  uni.navigateTo({ url: '/pages/appeal/create' })
}

function goDetail(id) {
  uni.navigateTo({ url: '/pages/appeal/detail?id=' + id })
}
</script>

<style lang="scss" scoped>
.page-appeal-list {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding-bottom: 120rpx;
}

.tabs {
  background-color: #ffffff;
  padding: 0 24rpx;
  margin-bottom: 16rpx;

  &__scroll {
    white-space: nowrap;
  }

  &__item {
    display: inline-flex;
    padding: 24rpx 28rpx;
    margin-right: 16rpx;
    position: relative;

    &--active {
      .tabs__text {
        color: #2563EB;
        font-weight: 600;
      }
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 40rpx;
        height: 6rpx;
        background-color: #2563EB;
        border-radius: 3rpx;
      }
    }
  }

  &__text {
    font-size: 28rpx;
    color: #666666;
  }
}

.list-scroll {
  height: calc(100vh - 120rpx);
}

.appeal-card {
  background-color: #ffffff;
  margin: 0 24rpx 16rpx;
  padding: 28rpx 32rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12rpx;
  }

  &__title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333333;
    flex: 1;
    margin-right: 16rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__content {
    font-size: 26rpx;
    color: #666666;
    line-height: 1.6;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    margin-bottom: 16rpx;
  }

  &__footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__type {
    font-size: 22rpx;
    color: #2563EB;
    background-color: rgba(26, 115, 232, 0.08);
    padding: 4rpx 16rpx;
    border-radius: 6rpx;
  }

  &__time {
    font-size: 22rpx;
    color: #cccccc;
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

.fab {
  position: fixed;
  right: 40rpx;
  bottom: 160rpx;
  width: 104rpx;
  height: 104rpx;
  background: linear-gradient(135deg, #2563EB, #3b82f6);
  border-radius: 52rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(26, 115, 232, 0.4);

  &__icon {
    font-size: 52rpx;
    color: #ffffff;
    line-height: 1;
  }
}
</style>
