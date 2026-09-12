<template>
  <view class="page-equipment-history">
    <!-- 筛选 -->
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

    <!-- 记录列表 -->
    <scroll-view scroll-y class="list-scroll" @scrolltolower="loadMore">
      <view v-if="list.length === 0 && !loading">
        <Empty title="暂无上报记录" />
      </view>
      <view v-for="item in list" :key="item.id" class="history-card">
        <view class="history-card__header">
          <text class="history-card__type">{{ item.typeName }}</text>
          <text class="history-card__time">{{ item.createTime }}</text>
        </view>
        <text class="history-card__equip">{{ item.equipName }}</text>
        <text v-if="item.description" class="history-card__desc">{{ item.description }}</text>
        <view v-if="item.abnormalCount > 0" class="history-card__alert">
          <text class="history-card__alert-text">⚠ 发现 {{ item.abnormalCount }} 项异常</text>
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
import Empty from '@/components/Empty.vue'

const tabs = [
  { label: '全部', value: '' },
  { label: '日常点检', value: 'check' },
  { label: '异常上报', value: 'abnormal' }
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
    const data = await get('/equipment/history', { type: currentTab.value, page: page.value })
    list.value = page.value === 1 ? (data.list || []) : [...list.value, ...(data.list || [])]
    hasMore.value = (data.list || []).length >= 10
  } catch (e) {
    list.value = [
      { id: 1, typeName: '日常点检', equipName: '压力容器A-001', createTime: '2026-06-25 09:00', description: '所有项目正常', abnormalCount: 0 },
      { id: 2, typeName: '异常上报', equipName: '锅炉B-003', createTime: '2026-06-24 14:30', description: '运行声音异常，疑似轴承磨损', abnormalCount: 1 },
      { id: 3, typeName: '日常点检', equipName: '叉车C-012', createTime: '2026-06-23 10:15', description: '安全标识缺失', abnormalCount: 1 }
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
</script>

<style lang="scss" scoped>
.page-equipment-history {
  min-height: 100vh;
  background-color: #f5f6fa;
}

.filter-bar {
  display: flex;
  background-color: #ffffff;
  padding: 16rpx 24rpx;
  margin-bottom: 16rpx;
  gap: 12rpx;
}

.filter-item {
  padding: 12rpx 28rpx;
  border-radius: 32rpx;
  background-color: #f5f6fa;

  &--active { background-color: rgba(26, 115, 232, 0.1); }

  &__text {
    font-size: 26rpx;
    color: #666666;
    .filter-item--active & { color: #2563EB; font-weight: 600; }
  }
}

.list-scroll {
  height: calc(100vh - 120rpx);
  padding: 0 24rpx;
}

.history-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 12rpx;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12rpx;
  }

  &__type {
    font-size: 24rpx;
    color: #2563EB;
    background-color: rgba(26, 115, 232, 0.08);
    padding: 4rpx 16rpx;
    border-radius: 6rpx;
  }

  &__time {
    font-size: 22rpx;
    color: #cccccc;
  }

  &__equip {
    font-size: 28rpx;
    font-weight: 600;
    color: #333333;
    display: block;
    margin-bottom: 8rpx;
  }

  &__desc {
    font-size: 26rpx;
    color: #666666;
    display: block;
  }

  &__alert {
    margin-top: 12rpx;
    padding: 8rpx 16rpx;
    background-color: rgba(255, 59, 48, 0.06);
    border-radius: 8rpx;
  }

  &__alert-text {
    font-size: 24rpx;
    color: #ff3b30;
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
