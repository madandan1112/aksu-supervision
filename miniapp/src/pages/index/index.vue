<template>
  <view class="page-index">
    <!-- ===== 执法人员工作台 ===== -->
    <view v-if="userStore.isInspector" class="insp">
      <view class="insp__hero" :style="{ paddingTop: (statusBarHeight + 10) + 'px' }">
        <view class="insp__hero-top">
          <view>
            <text class="insp__hero-date">{{ inspDate }}</text>
            <text class="insp__hero-title">执法工作台</text>
          </view>
          <view class="insp__hero-org">
            <text class="insp__hero-org-text">{{ userStore.userName || '执法员' }}</text>
          </view>
        </view>
        <view class="insp__stats">
          <view class="insp__stat" @tap="goInspTab('/pages/alerts/list')">
            <text class="insp__stat-num">{{ inspStats.pending }}</text>
            <text class="insp__stat-label">待处理预警</text>
          </view>
          <view class="insp__stat" @tap="goInspTab('/pages/inspection/create')">
            <text class="insp__stat-num">{{ inspStats.monthInspections }}</text>
            <text class="insp__stat-label">本月检查</text>
          </view>
          <view class="insp__stat" @tap="goInspTab('/pages/scan/index')">
            <text class="insp__stat-num">扫码</text>
            <text class="insp__stat-label">查企入口</text>
          </view>
        </view>
      </view>

      <view class="insp__body">
        <view class="insp__section">
          <view class="insp__section-head">
            <text class="insp__section-title">属地预警</text>
            <text class="insp__section-more" @tap="goInspTab('/pages/alerts/list')">全部 ›</text>
          </view>
          <view v-if="inspAlerts.length === 0" class="insp__empty">
            <text class="insp__empty-text">暂无待处理预警</text>
          </view>
          <view v-for="a in inspAlerts" :key="a.id" class="insp__alert" @tap="goInspTab('/pages/alerts/list')">
            <view class="insp__alert-head">
              <view class="insp__alert-level" :class="'insp__alert-level--' + (a.alertLevel || 'MEDIUM')">
                <text class="insp__alert-level-text">{{ levelText(a.alertLevel) }}</text>
              </view>
              <text class="insp__alert-no">#{{ a.id }}</text>
            </view>
            <text class="insp__alert-title">{{ a.title }}</text>
            <text class="insp__alert-desc">{{ a.content ? a.content.substring(0, 40) : '' }}</text>
          </view>
        </view>

        <view class="insp__section">
          <view class="insp__section-head">
            <text class="insp__section-title">快捷执法</text>
          </view>
          <view class="insp__quick">
            <view class="insp__quick-item" @tap="goInspTab('/pages/inspection/create')">
              <view class="insp__quick-icon insp__quick-icon--red"><text class="insp__quick-icon-text">检</text></view>
              <text class="insp__quick-label">现场检查</text>
            </view>
            <view class="insp__quick-item" @tap="goInspTab('/pages/scan/index')">
              <view class="insp__quick-icon insp__quick-icon--blue"><text class="insp__quick-icon-text">扫</text></view>
              <text class="insp__quick-label">扫码查企</text>
            </view>
            <view class="insp__quick-item" @tap="goInspTab('/pages/alerts/list')">
              <view class="insp__quick-icon insp__quick-icon--amber"><text class="insp__quick-icon-text">警</text></view>
              <text class="insp__quick-label">预警处置</text>
            </view>
            <view class="insp__quick-item" @tap="goInspTab('/pages/inspector/profile')">
              <view class="insp__quick-icon insp__quick-icon--gray"><text class="insp__quick-icon-text">我</text></view>
              <text class="insp__quick-label">我的</text>
            </view>
          </view>
        </view>
      </view>
      <InspTabBar active="/pages/index/index" />
    </view>

    <!-- ===== 企业商户首页 ===== -->
    <view v-if="!userStore.isInspector">
    <!-- 自定义导航栏 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header__content">
        <view class="header__greeting">
          <text class="header__hello">你好，{{ userStore.userName || '用户' }}</text>
          <text class="header__enterprise">{{ userStore.enterpriseName || '阿克苏监管平台' }}</text>
        </view>
        <view class="header__actions">
          <view class="header__scan" @tap="goScan">
            <text class="header__icon">📷</text>
          </view>
          <view class="header__badge" @tap="goMessage">
            <text class="header__icon">🔔</text>
            <view v-if="unreadCount > 0" class="header__dot">
              <text class="header__dot-text">{{ unreadCount > 99 ? '99+' : unreadCount }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="body" :style="{ paddingTop: (statusBarHeight + 56) + 'px' }">
      <!-- 快捷入口 -->
      <view class="quick-entry">
        <view class="quick-entry__item" @tap="goPage('/pages/appeal/create')">
          <view class="quick-entry__icon quick-entry__icon--blue">📋</view>
          <text class="quick-entry__label">诉求直达</text>
        </view>
        <view class="quick-entry__item" @tap="goPage('/pages/rectification/list')">
          <view class="quick-entry__icon quick-entry__icon--orange">🔧</view>
          <text class="quick-entry__label">整改响应</text>
        </view>
        <view class="quick-entry__item" @tap="goPage('/pages/report/list')">
          <view class="quick-entry__icon quick-entry__icon--green">📊</view>
          <text class="quick-entry__label">报告管理</text>
        </view>
        <view class="quick-entry__item" @tap="goScan">
          <view class="quick-entry__icon quick-entry__icon--purple">📷</view>
          <text class="quick-entry__label">扫码查询</text>
        </view>
      </view>

      <!-- 待办事项提醒 -->
      <view class="section">
        <view class="section__header">
          <text class="section__title">待办提醒</text>
          <text class="section__more" @tap="goPage('/pages/rectification/list')">查看全部 ›</text>
        </view>
        <view v-if="todoList.length === 0" class="section__empty">
          <text class="section__empty-text">暂无待办事项</text>
        </view>
        <view
          v-for="item in todoList"
          :key="item.id"
          class="todo-item"
          @tap="goPage(item.url)"
        >
          <view class="todo-item__left">
            <view class="todo-item__tag" :class="'todo-item__tag--' + item.level">
              <text class="todo-item__tag-text">{{ item.type }}</text>
            </view>
          </view>
          <view class="todo-item__center">
            <text class="todo-item__title">{{ item.title }}</text>
            <text class="todo-item__time">{{ item.deadline }}</text>
          </view>
          <text class="todo-item__arrow">›</text>
        </view>
      </view>

      <!-- 最近消息 -->
      <view class="section">
        <view class="section__header">
          <text class="section__title">最近消息</text>
          <text class="section__more" @tap="goPage('/pages/message/list')">查看全部 ›</text>
        </view>
        <view v-if="messageList.length === 0" class="section__empty">
          <text class="section__empty-text">暂无消息</text>
        </view>
        <view
          v-for="msg in messageList"
          :key="msg.id"
          class="msg-item"
          @tap="goPage('/pages/message/detail?id=' + msg.id)"
        >
          <view class="msg-item__icon-wrap" :class="'msg-item__icon-wrap--' + msg.category">
            <text class="msg-item__icon">{{ getCategoryIcon(msg.category) }}</text>
          </view>
          <view class="msg-item__content">
            <view class="msg-item__top">
              <text class="msg-item__title">{{ msg.title }}</text>
              <text class="msg-item__time">{{ msg.time }}</text>
            </view>
            <text class="msg-item__summary">{{ msg.summary }}</text>
          </view>
          <view v-if="!msg.read" class="msg-item__unread"></view>
        </view>
      </view>
    </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { get } from '@/utils/request'
import { timeAgo } from '@/utils/format'
import InspTabBar from '@/components/InspTabBar.vue'

const userStore = useUserStore()

const statusBarHeight = ref(0)
const todoList = ref([])
const messageList = ref([])
const unreadCount = ref(0)

/* ===== 执法工作台数据 ===== */
const inspAlerts = ref([])
const inspStats = ref({ pending: 0, monthInspections: 0 })
const inspDate = computed(() => {
  const d = new Date()
  const week = ['日', '一', '二', '三', '四', '五', '六'][d.getDay()]
  return `${d.getMonth() + 1}月${d.getDate()}日 星期${week}`
})

function levelText(level) {
  if (level === 'HIGH') return '高'
  if (level === 'MEDIUM') return '中'
  return '低'
}

function goInspTab(path) {
  uni.reLaunch({ url: path })
}

async function loadInspectorData() {
  const [alertRes, statRes] = await Promise.allSettled([
    get('/inspector/alert/list', { page: 1, size: 5, status: 'PENDING' }),
    get('/inspector/alert/statistics')
  ])
  if (alertRes.status === 'fulfilled') {
    const list = alertRes.value?.list || alertRes.value?.records || []
    inspAlerts.value = Array.isArray(list) ? list : []
  }
  if (statRes.status === 'fulfilled') {
    const s = statRes.value || {}
    inspStats.value = {
      pending: s.pending ?? s.PENDING ?? inspAlerts.value.length,
      monthInspections: s.monthInspections ?? s.monthCount ?? 0
    }
  }
}

const sysInfo = uni.getSystemInfoSync()
statusBarHeight.value = sysInfo.statusBarHeight || 0

onShow(() => {
  if (!userStore.isLoggedIn) return
  if (userStore.isInspector) {
    // 执法端：隐藏原生 tabBar，使用自定义药丸导航
    uni.hideTabBar({ animation: false }).catch?.(() => {})
    loadInspectorData()
  } else {
    uni.showTabBar({ animation: false }).catch?.(() => {})
    loadHomeData()
  }
})

async function loadHomeData() {
  try {
    // 并行加载待办和消息
    const [rectData, msgData] = await Promise.allSettled([
      get('/enterprise/rectification/list', { page: 1, size: 5 }),
      get('/message/list', { page: 1, size: 5 })
    ])

    // 处理整改待办
    if (rectData.status === 'fulfilled' && rectData.value?.list) {
      todoList.value = rectData.value.list.map(item => ({
        id: item.id,
        type: '整改',
        title: item.title || item.noticeNo || '整改通知',
        deadline: item.deadline ? item.deadline.substring(0, 10) : '',
        level: 'danger',
        url: '/pages/rectification/detail?id=' + item.id
      }))
    }

    // 处理消息
    if (msgData.status === 'fulfilled' && msgData.value?.list) {
      messageList.value = msgData.value.list.map(item => ({
        id: item.id,
        title: item.title,
        summary: item.content ? item.content.substring(0, 30) : '',
        time: timeAgo(item.createTime),
        category: item.category || 'system',
        read: item.read || false
      }))
      unreadCount.value = msgData.value.list.filter(m => !m.read).length
    }
  } catch (e) {
    console.error('加载首页数据失败', e)
  }
}

function getCategoryIcon(category) {
  const map = {
    appeal: '📋',
    rectification: '🔧',
    report: '📊',
    equipment: '⚙️',
    system: '📢'
  }
  return map[category] || '📩'
}

function goPage(url) {
  uni.navigateTo({ url })
}

function goMessage() {
  uni.navigateTo({ url: '/pages/message/list' })
}

function goScan() {
  uni.navigateTo({ url: '/pages/scan/index' })
}
</script>

<style lang="scss" scoped>
.page-index {
  min-height: 100vh;
  background-color: #f5f6fa;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: linear-gradient(135deg, #2563EB, #3b82f6);
  padding-bottom: 24rpx;

  &__content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16rpx 32rpx;
  }

  &__greeting {
    display: flex;
    flex-direction: column;
  }

  &__hello {
    font-size: 36rpx;
    font-weight: 600;
    color: #ffffff;
  }

  &__enterprise {
    font-size: 24rpx;
    color: rgba(255, 255, 255, 0.8);
    margin-top: 4rpx;
  }

  &__actions {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  &__scan, &__badge {
    position: relative;
    width: 72rpx;
    height: 72rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__icon {
    font-size: 44rpx;
  }

  &__dot {
    position: absolute;
    top: 4rpx;
    right: 4rpx;
    min-width: 32rpx;
    height: 32rpx;
    background-color: #ff3b30;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 6rpx;
  }

  &__dot-text {
    font-size: 18rpx;
    color: #ffffff;
  }
}

.body {
  padding: 24rpx 24rpx 120rpx;
}

.quick-entry {
  display: flex;
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 32rpx 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  margin-bottom: 24rpx;

  &__item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  &__icon {
    width: 96rpx;
    height: 96rpx;
    border-radius: 24rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 44rpx;
    margin-bottom: 12rpx;

    &--blue { background-color: rgba(26, 115, 232, 0.1); }
    &--orange { background-color: rgba(255, 149, 0, 0.1); }
    &--green { background-color: rgba(52, 199, 89, 0.1); }
    &--purple { background-color: rgba(175, 82, 222, 0.1); }
  }

  &__label {
    font-size: 24rpx;
    color: #333333;
  }
}

.section {
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 24rpx 32rpx;
  margin-bottom: 24rpx;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24rpx;
  }

  &__title {
    font-size: 32rpx;
    font-weight: 600;
    color: #333333;
  }

  &__more {
    font-size: 24rpx;
    color: #999999;
  }

  &__empty {
    padding: 40rpx 0;
    text-align: center;
  }

  &__empty-text {
    font-size: 26rpx;
    color: #cccccc;
  }
}

.todo-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child { border-bottom: none; }

  &__left { margin-right: 20rpx; }

  &__tag {
    padding: 6rpx 16rpx;
    border-radius: 8rpx;

    &--danger { background-color: rgba(255, 59, 48, 0.1); }
    &--warning { background-color: rgba(255, 149, 0, 0.1); }
    &--info { background-color: rgba(26, 115, 232, 0.1); }
  }

  &__tag-text {
    font-size: 22rpx;
    font-weight: 600;

    .todo-item__tag--danger & { color: #ff3b30; }
    .todo-item__tag--warning & { color: #ff9500; }
    .todo-item__tag--info & { color: #2563EB; }
  }

  &__center { flex: 1; }

  &__title {
    font-size: 28rpx;
    color: #333333;
    display: block;
  }

  &__time {
    font-size: 24rpx;
    color: #999999;
    margin-top: 6rpx;
    display: block;
  }

  &__arrow {
    font-size: 32rpx;
    color: #cccccc;
    margin-left: 12rpx;
  }
}

.msg-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child { border-bottom: none; }

  &__icon-wrap {
    width: 72rpx;
    height: 72rpx;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20rpx;
    font-size: 36rpx;

    &--appeal { background-color: rgba(26, 115, 232, 0.1); }
    &--rectification { background-color: rgba(255, 149, 0, 0.1); }
    &--report { background-color: rgba(52, 199, 89, 0.1); }
    &--equipment { background-color: rgba(175, 82, 222, 0.1); }
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

  &__title {
    font-size: 28rpx;
    color: #333333;
    font-weight: 500;
  }

  &__time {
    font-size: 22rpx;
    color: #cccccc;
  }

  &__summary {
    font-size: 24rpx;
    color: #999999;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__unread {
    width: 16rpx;
    height: 16rpx;
    background-color: #ff3b30;
    border-radius: 50%;
    margin-left: 12rpx;
  }
}

/* ===== 执法工作台 ===== */
.insp {
  min-height: 100vh;
  background-color: #f6f7f9;
  padding-bottom: 180rpx;

  &__hero {
    background: linear-gradient(165deg, #c8102e 0%, #a50d22 100%);
    padding: 20rpx 32rpx 36rpx;
    border-radius: 0 0 44rpx 44rpx;
  }

  &__hero-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__hero-date {
    display: block;
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.75);
  }

  &__hero-title {
    display: block;
    font-size: 44rpx;
    font-weight: 800;
    color: #ffffff;
    margin-top: 8rpx;
  }

  &__hero-org {
    background-color: rgba(255, 255, 255, 0.16);
    border-radius: 20rpx;
    padding: 12rpx 24rpx;
  }

  &__hero-org-text {
    font-size: 24rpx;
    color: #ffffff;
  }

  &__stats {
    display: flex;
    gap: 16rpx;
    margin-top: 32rpx;
  }

  &__stat {
    flex: 1;
    background-color: rgba(255, 255, 255, 0.14);
    border: 1rpx solid rgba(255, 255, 255, 0.16);
    border-radius: 24rpx;
    padding: 20rpx 0;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  &__stat-num {
    font-size: 40rpx;
    font-weight: 800;
    color: #ffffff;
  }

  &__stat-label {
    font-size: 20rpx;
    color: rgba(255, 255, 255, 0.8);
    margin-top: 6rpx;
  }

  &__body {
    padding: 28rpx 28rpx 0;
  }

  &__section {
    margin-bottom: 32rpx;
  }

  &__section-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20rpx;
  }

  &__section-title {
    font-size: 32rpx;
    font-weight: 700;
    color: #18181b;
  }

  &__section-more {
    font-size: 24rpx;
    color: #a1a1aa;
  }

  &__empty {
    background-color: #ffffff;
    border-radius: 28rpx;
    padding: 48rpx 0;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__empty-text {
    font-size: 26rpx;
    color: #a1a1aa;
  }

  &__alert {
    background-color: #ffffff;
    border: 1rpx solid #f4cfd6;
    border-radius: 28rpx;
    padding: 28rpx;
    margin-bottom: 20rpx;
  }

  &__alert-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 14rpx;
  }

  &__alert-level {
    border-radius: 10rpx;
    padding: 4rpx 14rpx;

    &--HIGH {
      background-color: #c8102e;
    }
    &--MEDIUM {
      background-color: #f59e0b;
    }
    &--LOW {
      background-color: #a1a1aa;
    }
  }

  &__alert-level-text {
    font-size: 22rpx;
    font-weight: 700;
    color: #ffffff;
  }

  &__alert-no {
    font-size: 22rpx;
    color: #a1a1aa;
  }

  &__alert-title {
    display: block;
    font-size: 30rpx;
    font-weight: 600;
    color: #18181b;
  }

  &__alert-desc {
    display: block;
    font-size: 24rpx;
    color: #71717a;
    margin-top: 8rpx;
  }

  &__quick {
    display: flex;
    gap: 16rpx;
  }

  &__quick-item {
    flex: 1;
    background-color: #ffffff;
    border-radius: 28rpx;
    border: 1rpx solid #ececf0;
    padding: 28rpx 0;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  &__quick-icon {
    width: 76rpx;
    height: 76rpx;
    border-radius: 22rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 14rpx;

    &--red {
      background-color: #fae7ea;
    }
    &--blue {
      background-color: #dbeafe;
    }
    &--amber {
      background-color: #fef3c7;
    }
    &--gray {
      background-color: #f4f4f5;
    }
  }

  &__quick-icon-text {
    font-size: 32rpx;
    font-weight: 700;
  }

  &__quick-icon--red &__quick-icon-text {
    color: #c8102e;
  }
  &__quick-icon--blue &__quick-icon-text {
    color: #2563eb;
  }
  &__quick-icon--amber &__quick-icon-text {
    color: #d97706;
  }
  &__quick-icon--gray &__quick-icon-text {
    color: #71717a;
  }

  &__quick-label {
    font-size: 24rpx;
    color: #52525b;
  }
}

</style>
