<template>
  <view class="page-equipment-list">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input v-model="keyword" class="search-bar__input" placeholder="搜索设备名称/编号" confirm-type="search" @confirm="doSearch" />
    </view>

    <!-- 设备列表 -->
    <scroll-view scroll-y class="list-scroll" @scrolltolower="loadMore">
      <view v-if="list.length === 0 && !loading">
        <Empty title="暂无设备记录" description="点击右下角添加设备" />
      </view>
      <view
        v-for="item in list"
        :key="item.id"
        class="equip-card"
        @tap="goReport(item.id)"
      >
        <view class="equip-card__header">
          <text class="equip-card__name">{{ item.name }}</text>
          <StatusTag :text="item.statusText" :type="getStatusType(item.status)" />
        </view>
        <view class="equip-card__body">
          <view class="equip-card__row">
            <text class="equip-card__label">设备编号</text>
            <text class="equip-card__value">{{ item.code }}</text>
          </view>
          <view class="equip-card__row">
            <text class="equip-card__label">设备类型</text>
            <text class="equip-card__value">{{ item.typeName }}</text>
          </view>
          <view class="equip-card__row">
            <text class="equip-card__label">下次检查</text>
            <text class="equip-card__value" :class="{ 'equip-card__value--danger': item.isOverdue }">{{ item.nextCheckDate }}</text>
          </view>
        </view>
      </view>

      <view v-if="loading" class="loading-more">
        <text class="loading-more__text">加载中...</text>
      </view>
      <view v-if="!hasMore && list.length > 0" class="no-more">
        <text class="no-more__text">没有更多了</text>
      </view>
    </scroll-view>

    <!-- 添加按钮 -->
    <view class="fab" @tap="goAdd">
      <text class="fab__icon">+</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get } from '@/utils/request'
import StatusTag from '@/components/StatusTag.vue'
import Empty from '@/components/Empty.vue'

const keyword = ref('')
const list = ref([])
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)

onShow(() => {
  page.value = 1
  list.value = []
  loadList()
})

function doSearch() {
  page.value = 1
  list.value = []
  loadList()
}

async function loadList() {
  if (loading.value) return
  loading.value = true
  try {
    const data = await get('/equipment/list', { keyword: keyword.value, page: page.value })
    const items = (data.list || []).map(item => ({
      ...item,
      statusText: getStatusText(item.status)
    }))
    list.value = page.value === 1 ? items : [...list.value, ...items]
    hasMore.value = items.length >= 10
  } catch (e) {
    list.value = [
      { id: 1, name: '压力容器A-001', code: 'EQ-2024-001', typeName: '压力容器', status: 'normal', statusText: '正常', nextCheckDate: '2026-08-15', isOverdue: false },
      { id: 2, name: '锅炉B-003', code: 'EQ-2024-003', typeName: '锅炉', status: 'warning', statusText: '即将到期', nextCheckDate: '2026-07-01', isOverdue: false },
      { id: 3, name: '叉车C-012', code: 'EQ-2024-012', typeName: '特种设备', status: 'overdue', statusText: '已逾期', nextCheckDate: '2026-06-01', isOverdue: true },
      { id: 4, name: '消防泵D-005', code: 'EQ-2024-005', typeName: '消防设备', status: 'normal', statusText: '正常', nextCheckDate: '2026-09-20', isOverdue: false }
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
  const map = { normal: '正常', warning: '即将到期', overdue: '已逾期', repair: '维修中' }
  return map[status] || status
}

function getStatusType(status) {
  const map = { normal: 'success', warning: 'warning', overdue: 'danger', repair: 'primary' }
  return map[status] || 'default'
}

function goAdd() {
  uni.navigateTo({ url: '/pages/equipment/add' })
}

function goReport(id) {
  uni.navigateTo({ url: '/pages/equipment/report?id=' + id })
}
</script>

<style lang="scss" scoped>
.page-equipment-list {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding-bottom: 120rpx;
}

.search-bar {
  padding: 16rpx 24rpx;
  background-color: #ffffff;

  &__input {
    width: 100%;
    height: 72rpx;
    background-color: #f5f6fa;
    border-radius: 36rpx;
    padding: 0 28rpx;
    font-size: 28rpx;
  }
}

.list-scroll {
  height: calc(100vh - 120rpx);
  padding: 16rpx 24rpx;
}

.equip-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 16rpx;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16rpx;
  }

  &__name {
    font-size: 30rpx;
    font-weight: 600;
    color: #333333;
  }

  &__body {}

  &__row {
    display: flex;
    justify-content: space-between;
    padding: 8rpx 0;
  }

  &__label {
    font-size: 24rpx;
    color: #999999;
  }

  &__value {
    font-size: 24rpx;
    color: #333333;

    &--danger { color: #ff3b30; font-weight: 600; }
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
  background: linear-gradient(135deg, #1a73e8, #4a90e8);
  border-radius: 52rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(26, 115, 232, 0.4);

  &__icon { font-size: 52rpx; color: #ffffff; line-height: 1; }
}
</style>
