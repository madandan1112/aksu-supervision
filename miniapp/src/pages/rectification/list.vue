<template>
  <view class="page-rectification-list">
    <!-- 状态筛选 -->
    <view class="filter-bar">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        class="filter-item"
        :class="{ 'filter-item--active': currentTab === tab.value }"
        @tap="switchTab(tab.value)"
      >
        <text class="filter-item__text">{{ tab.label }}</text>
      </view>
    </view>

    <!-- 列表 -->
    <scroll-view scroll-y class="list-scroll" @scrolltolower="loadMore">
      <view v-if="list.length === 0 && !loading">
        <Empty title="暂无整改通知" />
      </view>
      <view
        v-for="item in list"
        :key="item.id"
        class="rect-card"
        @tap="goDetail(item.id)"
      >
        <view class="rect-card__header">
          <text class="rect-card__title">{{ item.title }}</text>
          <StatusTag :text="item.statusText" :type="getStatusType(item.status)" />
        </view>
        <text class="rect-card__requirement">{{ item.requirement }}</text>
        <view class="rect-card__footer">
          <view class="rect-card__deadline">
            <text class="rect-card__deadline-icon">⏰</text>
            <text class="rect-card__deadline-text" :class="{ 'rect-card__deadline-text--urgent': item.isUrgent }">
              截止：{{ item.deadline }}
            </text>
          </view>
          <text class="rect-card__source">{{ item.source }}</text>
        </view>
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
import { formatDate } from '@/utils/format'
import StatusTag from '@/components/StatusTag.vue'
import Empty from '@/components/Empty.vue'

const tabs = [
  { label: '全部', value: '' },
  { label: '待整改', value: 'pending' },
  { label: '整改中', value: 'rectifying' },
  { label: '待验收', value: 'verifying' },
  { label: '已通过', value: 'passed' },
  { label: '未通过', value: 'failed' }
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
    const data = await get('/rectification/list', { status: currentTab.value, page: page.value })
    const items = (data.list || []).map(item => ({
      ...item,
      statusText: getStatusText(item.status)
    }))
    list.value = page.value === 1 ? items : [...list.value, ...items]
    hasMore.value = items.length >= 10
  } catch (e) {
    list.value = [
      { id: 1, title: '消防设施整改通知', requirement: '请于限期内完成消防栓维修及灭火器更换工作', deadline: '2026-07-05', status: 'pending', statusText: '待整改', source: '消防检查', isUrgent: true },
      { id: 2, title: '废气排放口整改', requirement: '排放口标识不清，需重新设置标识牌', deadline: '2026-07-15', status: 'rectifying', statusText: '整改中', source: '环保检查', isUrgent: false },
      { id: 3, title: '安全通道整改验收', requirement: '安全通道堆放杂物问题已整改完毕，等待验收', deadline: '2026-07-01', status: 'verifying', statusText: '待验收', source: '安全检查', isUrgent: false },
      { id: 4, title: '压力表定期校验', requirement: '压力表已校验合格', deadline: '2026-06-25', status: 'passed', statusText: '已通过', source: '特种设备检查', isUrgent: false }
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
  const map = { pending: '待整改', rectifying: '整改中', verifying: '待验收', passed: '已通过', failed: '未通过' }
  return map[status] || status
}

function getStatusType(status) {
  const map = { pending: 'danger', rectifying: 'warning', verifying: 'primary', passed: 'success', failed: 'danger' }
  return map[status] || 'default'
}

function goDetail(id) {
  uni.navigateTo({ url: '/pages/rectification/detail?id=' + id })
}
</script>

<style lang="scss" scoped>
.page-rectification-list {
  min-height: 100vh;
  background-color: #f5f6fa;
}

.filter-bar {
  display: flex;
  background-color: #ffffff;
  padding: 16rpx 24rpx;
  margin-bottom: 16rpx;
  overflow-x: auto;
}

.filter-item {
  flex-shrink: 0;
  padding: 12rpx 28rpx;
  margin-right: 12rpx;
  border-radius: 32rpx;
  background-color: #f5f6fa;

  &--active {
    background-color: rgba(26, 115, 232, 0.1);
  }

  &__text {
    font-size: 26rpx;
    color: #666666;

    .filter-item--active & {
      color: #2563EB;
      font-weight: 600;
    }
  }
}

.list-scroll {
  height: calc(100vh - 120rpx);
  padding: 0 24rpx 40rpx;
}

.rect-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx 32rpx;
  margin-bottom: 16rpx;
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
  }

  &__requirement {
    font-size: 26rpx;
    color: #666666;
    line-height: 1.6;
    margin-bottom: 16rpx;
    display: block;
  }

  &__footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__deadline {
    display: flex;
    align-items: center;
    gap: 6rpx;
  }

  &__deadline-icon { font-size: 24rpx; }

  &__deadline-text {
    font-size: 22rpx;
    color: #999999;

    &--urgent { color: #ff3b30; }
  }

  &__source {
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
</style>
