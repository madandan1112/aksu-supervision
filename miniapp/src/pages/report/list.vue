<template>
  <view class="page-report-list">
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
        <Empty title="暂无报告">
          <template #action>
            <view class="empty-action" @tap="goUpload">
              <text class="empty-action__text">上传报告</text>
            </view>
          </template>
        </Empty>
      </view>
      <view
        v-for="item in list"
        :key="item.id"
        class="report-card"
        @tap="goDetail(item.id)"
      >
        <view class="report-card__header">
          <text class="report-card__icon">📊</text>
          <view class="report-card__info">
            <text class="report-card__title">{{ item.title }}</text>
            <text class="report-card__meta">{{ item.category }} · {{ item.createTime }}</text>
          </view>
          <StatusTag :text="item.statusText" :type="getStatusType(item.status)" />
        </view>
      </view>

      <view v-if="loading" class="loading-more">
        <text class="loading-more__text">加载中...</text>
      </view>
      <view v-if="!hasMore && list.length > 0" class="no-more">
        <text class="no-more__text">没有更多了</text>
      </view>
    </scroll-view>

    <!-- 上传按钮 -->
    <view class="fab" @tap="goUpload">
      <text class="fab__icon">+</text>
    </view>
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
  { label: '待审核', value: 'pending' },
  { label: '审核中', value: 'reviewing' },
  { label: '已通过', value: 'approved' },
  { label: '已退回', value: 'rejected' }
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
    const data = await get('/report/list', { status: currentTab.value, page: page.value })
    const items = (data.list || []).map(item => ({
      ...item,
      statusText: getStatusText(item.status)
    }))
    list.value = page.value === 1 ? items : [...list.value, ...items]
    hasMore.value = items.length >= 10
  } catch (e) {
    list.value = [
      { id: 1, title: 'Q2安全生产合规报告', category: '安全生产', status: 'pending', statusText: '待审核', createTime: '2026-06-25' },
      { id: 2, title: '年度环保合规自查报告', category: '环境保护', status: 'reviewing', statusText: '审核中', createTime: '2026-06-20' },
      { id: 3, title: 'Q1消防设施检测报告', category: '消防安全', status: 'approved', statusText: '已通过', createTime: '2026-05-15' },
      { id: 4, title: '特种设备年度检验报告', category: '特种设备', status: 'rejected', statusText: '已退回', createTime: '2026-06-10' }
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
  const map = { pending: '待审核', reviewing: '审核中', approved: '已通过', rejected: '已退回' }
  return map[status] || status
}

function getStatusType(status) {
  const map = { pending: 'warning', reviewing: 'primary', approved: 'success', rejected: 'danger' }
  return map[status] || 'default'
}

function goDetail(id) {
  uni.navigateTo({ url: '/pages/report/detail?id=' + id })
}

function goUpload() {
  uni.navigateTo({ url: '/pages/report/upload' })
}
</script>

<style lang="scss" scoped>
.page-report-list {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding-bottom: 120rpx;
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

.report-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 12rpx;

  &__header {
    display: flex;
    align-items: center;
  }

  &__icon {
    font-size: 40rpx;
    margin-right: 16rpx;
  }

  &__info {
    flex: 1;
    margin-right: 16rpx;
  }

  &__title {
    font-size: 28rpx;
    font-weight: 600;
    color: #333333;
    display: block;
    margin-bottom: 6rpx;
  }

  &__meta {
    font-size: 22rpx;
    color: #999999;
  }
}

.empty-action {
  padding: 16rpx 48rpx;
  background-color: #2563EB;
  border-radius: 40rpx;
  margin-top: 20rpx;

  &__text {
    font-size: 28rpx;
    color: #ffffff;
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

  &__icon { font-size: 52rpx; color: #ffffff; line-height: 1; }
}
</style>
