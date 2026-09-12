<template>
  <view class="page-scan">
    <!-- 扫码区域 -->
    <view v-if="!scanned" class="scan-area">
      <view class="scan-header">
        <text class="scan-header__title">扫码查询</text>
        <text class="scan-header__desc">扫描企业二维码查看信息</text>
      </view>

      <view class="scan-box" @tap="handleScan">
        <view class="scan-box__frame">
          <view class="scan-box__corner scan-box__corner--tl"></view>
          <view class="scan-box__corner scan-box__corner--tr"></view>
          <view class="scan-box__corner scan-box__corner--bl"></view>
          <view class="scan-box__corner scan-box__corner--br"></view>
          <text class="scan-box__icon">📷</text>
          <text class="scan-box__text">点击扫码</text>
        </view>
      </view>

      <!-- 手动输入 -->
      <view class="manual-input">
        <text class="manual-input__label">或手动输入企业信用代码：</text>
        <view class="manual-input__row">
          <input
            class="manual-input__field"
            v-model="creditCode"
            placeholder="统一社会信用代码"
            placeholder-class="manual-input__placeholder"
          />
          <button class="manual-input__btn" @tap="searchByCode">查询</button>
        </view>
      </view>
    </view>

    <!-- 扫码结果 - 企业用户视角 -->
    <view v-else-if="scanned && !userStore.isInspector" class="result-area">
      <view class="result-header">
        <view class="result-header__icon">🏢</view>
        <text class="result-header__title">{{ entInfo.name || '企业信息' }}</text>
        <text class="result-header__sub">{{ entInfo.creditCode || '' }}</text>
      </view>

      <!-- 基本信息 -->
      <view class="info-card">
        <text class="info-card__title">企业基本信息</text>
        <view class="info-row">
          <text class="info-row__label">法定代表人</text>
          <text class="info-row__value">{{ entInfo.legalPerson || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-row__label">行业</text>
          <text class="info-row__value">{{ entInfo.industry || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-row__label">区域</text>
          <text class="info-row__value">{{ entInfo.area || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-row__label">地址</text>
          <text class="info-row__value">{{ entInfo.address || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-row__label">经营范围</text>
          <text class="info-row__value">{{ entInfo.businessScope || '-' }}</text>
        </view>
      </view>

      <!-- 我的诉求 -->
      <view class="info-card">
        <text class="info-card__title">我的诉求</text>
        <view v-if="appeals.length === 0" class="info-card__empty">
          <text class="info-card__empty-text">暂无诉求记录</text>
        </view>
        <view v-for="item in appeals" :key="item.id" class="appeal-item" @tap="goAppealDetail(item.id)">
          <view class="appeal-item__header">
            <text class="appeal-item__title">{{ item.title }}</text>
            <view class="appeal-item__status" :class="'appeal-item__status--' + item.status">
              <text class="appeal-item__status-text">{{ statusMap[item.status] || item.status }}</text>
            </view>
          </view>
          <text class="appeal-item__time">{{ item.createTime }}</text>
        </view>
      </view>

      <!-- 我的整改 -->
      <view class="info-card">
        <text class="info-card__title">整改记录</text>
        <view v-if="rectifications.length === 0" class="info-card__empty">
          <text class="info-card__empty-text">暂无整改记录</text>
        </view>
        <view v-for="item in rectifications" :key="item.id" class="rect-item" @tap="goRectDetail(item.id)">
          <view class="rect-item__header">
            <text class="rect-item__title">{{ item.title || item.noticeNo }}</text>
            <view class="rect-item__status" :class="'rect-item__status--' + item.status">
              <text class="rect-item__status-text">{{ rectStatusMap[item.status] || item.status }}</text>
            </view>
          </view>
          <text class="rect-item__time">{{ item.deadline }}</text>
        </view>
      </view>

      <!-- 我的报告 -->
      <view class="info-card">
        <text class="info-card__title">合规报告</text>
        <view v-if="reports.length === 0" class="info-card__empty">
          <text class="info-card__empty-text">暂无报告</text>
        </view>
        <view v-for="item in reports" :key="item.id" class="report-item">
          <text class="report-item__name">{{ item.reportName || item.title }}</text>
          <text class="report-item__status">{{ item.status || '' }}</text>
        </view>
      </view>
    </view>

    <!-- 扫码结果 - 执法人员视角（全景档案） -->
    <view v-else-if="scanned && userStore.isInspector" class="result-area">
      <view class="result-header result-header--inspector">
        <view class="result-header__icon">🔍</view>
        <text class="result-header__title">{{ entInfo.name || '企业全景档案' }}</text>
        <text class="result-header__sub">{{ entInfo.creditCode || '' }}</text>
        <view class="result-header__badge result-header__badge--inspector">
          <text class="result-header__badge-text">执法视角</text>
        </view>
      </view>

      <!-- 企业详细信息 -->
      <view class="info-card info-card--inspector">
        <text class="info-card__title">企业详细信息</text>
        <view class="info-row">
          <text class="info-row__label">法定代表人</text>
          <text class="info-row__value">{{ entInfo.legalPerson || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-row__label">联系电话</text>
          <text class="info-row__value">{{ entInfo.phone || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-row__label">邮箱</text>
          <text class="info-row__value">{{ entInfo.email || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-row__label">行业</text>
          <text class="info-row__value">{{ entInfo.industry || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-row__label">区域</text>
          <text class="info-row__value">{{ entInfo.area || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-row__label">地址</text>
          <text class="info-row__value">{{ entInfo.address || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-row__label">经营范围</text>
          <text class="info-row__value">{{ entInfo.businessScope || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="info-row__label">状态</text>
          <text class="info-row__value">{{ entInfo.status === 1 ? '正常' : '其他' }}</text>
        </view>
      </view>

      <!-- 联系人 -->
      <view class="info-card info-card--inspector">
        <text class="info-card__title">企业联系人</text>
        <view v-if="contacts.length === 0" class="info-card__empty">
          <text class="info-card__empty-text">暂无联系人</text>
        </view>
        <view v-for="c in contacts" :key="c.id" class="contact-item">
          <view class="contact-item__info">
            <text class="contact-item__name">{{ c.contactName || c.name }}</text>
            <text class="contact-item__position">{{ c.position || '' }}</text>
          </view>
          <text class="contact-item__phone">{{ c.phone || '' }}</text>
        </view>
      </view>

      <!-- 检查记录 -->
      <view class="info-card info-card--inspector">
        <text class="info-card__title">检查记录</text>
        <view v-if="inspections.length === 0" class="info-card__empty">
          <text class="info-card__empty-text">暂无检查记录</text>
        </view>
        <view v-for="ins in inspections" :key="ins.id" class="insp-item">
          <view class="insp-item__header">
            <text class="insp-item__no">{{ ins.taskNo || ins.inspectionNo || '检查' }}</text>
            <text class="insp-item__result" :class="'insp-item__result--' + (ins.result || '')">{{ ins.result || '' }}</text>
          </view>
          <text class="insp-item__time">{{ ins.createTime || '' }}</text>
        </view>
      </view>

      <!-- 所有诉求 -->
      <view class="info-card info-card--inspector">
        <text class="info-card__title">诉求记录</text>
        <view v-if="appeals.length === 0" class="info-card__empty">
          <text class="info-card__empty-text">暂无诉求</text>
        </view>
        <view v-for="item in appeals" :key="item.id" class="appeal-item">
          <view class="appeal-item__header">
            <text class="appeal-item__title">{{ item.title }}</text>
            <view class="appeal-item__status" :class="'appeal-item__status--' + item.status">
              <text class="appeal-item__status-text">{{ statusMap[item.status] || item.status }}</text>
            </view>
          </view>
          <text class="appeal-item__time">{{ item.createTime }}</text>
        </view>
      </view>

      <!-- 所有整改 -->
      <view class="info-card info-card--inspector">
        <text class="info-card__title">整改记录</text>
        <view v-if="rectifications.length === 0" class="info-card__empty">
          <text class="info-card__empty-text">暂无整改</text>
        </view>
        <view v-for="item in rectifications" :key="item.id" class="rect-item">
          <view class="rect-item__header">
            <text class="rect-item__title">{{ item.title || item.noticeNo }}</text>
            <view class="rect-item__status" :class="'rect-item__status--' + item.status">
              <text class="rect-item__status-text">{{ rectStatusMap[item.status] || item.status }}</text>
            </view>
          </view>
          <text class="rect-item__time">{{ item.deadline }}</text>
        </view>
      </view>

      <!-- 合规报告 -->
      <view class="info-card info-card--inspector">
        <text class="info-card__title">合规报告</text>
        <view v-if="reports.length === 0" class="info-card__empty">
          <text class="info-card__empty-text">暂无报告</text>
        </view>
        <view v-for="item in reports" :key="item.id" class="report-item">
          <text class="report-item__name">{{ item.reportName || item.title }}</text>
          <text class="report-item__status">{{ item.status || '' }}</text>
        </view>
      </view>
    </view>

    <!-- 重新扫码 -->
    <view v-if="scanned" class="rescan-btn">
      <button class="btn-rescan" @tap="resetScan">重新扫码</button>
    </view>

    <InspTabBar v-if="userStore.isInspector" active="/pages/scan/index" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { get } from '@/utils/request'
import InspTabBar from '@/components/InspTabBar.vue'

const userStore = useUserStore()

onShow(() => {
  if (userStore.isInspector) {
    uni.hideTabBar({ animation: false }).catch?.(() => {})
  }
})

const scanned = ref(false)
const creditCode = ref('')
const entInfo = ref({})
const appeals = ref([])
const rectifications = ref([])
const reports = ref([])
const contacts = ref([])
const inspections = ref([])

const statusMap = {
  'PENDING': '待处理',
  'ASSIGNED': '已分流',
  'HANDLING': '处理中',
  'HANDLED': '已处理',
  'CLOSED': '已关闭'
}

const rectStatusMap = {
  'PENDING': '待整改',
  'IN_PROGRESS': '整改中',
  'SUBMITTED': '已提交',
  'ACCEPTED': '已验收',
  'REJECTED': '已驳回'
}

async function handleScan() {
  try {
    // #ifdef H5
    // H5 模式下模拟扫码，直接弹出输入框
    uni.showModal({
      title: '扫码查询',
      editable: true,
      placeholderText: '请输入企业ID或信用代码',
      success: async (res) => {
        if (res.confirm && res.content) {
          await loadEnterpriseInfo(res.content.trim())
        }
      }
    })
    // #endif
    // #ifndef H5
    const [err, scanRes] = await uni.scanCode({
      scanType: ['qrCode', 'barCode'],
      onlyFromCamera: false
    })
    if (!err && scanRes && scanRes.result) {
      await loadEnterpriseInfo(scanRes.result)
    } else if (err) {
      uni.showToast({ title: '扫码取消', icon: 'none' })
    }
    // #endif
  } catch (e) {
    uni.showToast({ title: '扫码失败', icon: 'none' })
  }
}

async function searchByCode() {
  if (!creditCode.value.trim()) {
    uni.showToast({ title: '请输入信用代码', icon: 'none' })
    return
  }
  await loadEnterpriseInfo(creditCode.value.trim())
}

async function loadEnterpriseInfo(code) {
  uni.showLoading({ title: '查询中...' })
  try {
    // 先根据 code 查企业信息
    const data = await get('/enterprise/scan', { code })
    entInfo.value = data.enterprise || data
    scanned.value = true

    const entId = data.enterprise?.id || data.id

    // 根据用户类型加载不同数据
    if (userStore.isInspector) {
      // 执法人员：加载全景档案
      const [appealData, rectData, reportData, contactData, inspData] = await Promise.allSettled([
        get('/inspector/appeal/list', { enterpriseId: entId, page: 1, size: 50 }),
        get('/inspector/rectification/pending', { enterpriseId: entId, page: 1, size: 50 }),
        get('/inspector/report/list', { enterpriseId: entId, page: 1, size: 50 }),
        get('/enterprise/contacts', { enterpriseId: entId }),
        get('/inspector/inspection/list', { enterpriseId: entId, page: 1, size: 50 })
      ])
      if (appealData.status === 'fulfilled') appeals.value = appealData.value?.list || []
      if (rectData.status === 'fulfilled') rectifications.value = rectData.value?.list || []
      if (reportData.status === 'fulfilled') reports.value = reportData.value?.list || []
      if (contactData.status === 'fulfilled') contacts.value = contactData.value || []
      if (inspData.status === 'fulfilled') inspections.value = inspData.value?.list || []
    } else {
      // 企业用户：加载自己的数据
      const [appealData, rectData, reportData] = await Promise.allSettled([
        get('/appeal/list', { enterpriseId: entId, page: 1, size: 20 }),
        get('/enterprise/rectification/list', { page: 1, size: 20 }),
        get('/report/list', { enterpriseId: entId, page: 1, size: 20 })
      ])
      if (appealData.status === 'fulfilled') appeals.value = appealData.value?.list || []
      if (rectData.status === 'fulfilled') rectifications.value = rectData.value?.list || []
      if (reportData.status === 'fulfilled') reports.value = reportData.value?.list || []
    }
  } catch (e) {
    uni.showToast({ title: e.message || '查询失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

function resetScan() {
  scanned.value = false
  entInfo.value = {}
  appeals.value = []
  rectifications.value = []
  reports.value = []
  contacts.value = []
  inspections.value = []
  creditCode.value = ''
}

function goAppealDetail(id) {
  uni.navigateTo({ url: '/pages/appeal/detail?id=' + id })
}

function goRectDetail(id) {
  uni.navigateTo({ url: '/pages/rectification/detail?id=' + id })
}
</script>

<style lang="scss" scoped>
.page-scan {
  min-height: 100vh;
  background-color: #f5f6fa;
}

.scan-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx 32rpx;
}

.scan-header {
  text-align: center;
  margin-bottom: 48rpx;

  &__title {
    font-size: 40rpx;
    font-weight: 700;
    color: #333333;
    display: block;
    margin-bottom: 12rpx;
  }

  &__desc {
    font-size: 26rpx;
    color: #999999;
  }
}

.scan-box {
  width: 500rpx;
  height: 500rpx;
  margin-bottom: 48rpx;

  &__frame {
    width: 100%;
    height: 100%;
    border: 4rpx dashed #cccccc;
    border-radius: 24rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
    background-color: #ffffff;
  }

  &__corner {
    position: absolute;
    width: 40rpx;
    height: 40rpx;
    border-color: #2563EB;
    border-style: solid;
    border-width: 0;

    &--tl { top: -2rpx; left: -2rpx; border-top-width: 8rpx; border-left-width: 8rpx; border-top-left-radius: 24rpx; }
    &--tr { top: -2rpx; right: -2rpx; border-top-width: 8rpx; border-right-width: 8rpx; border-top-right-radius: 24rpx; }
    &--bl { bottom: -2rpx; left: -2rpx; border-bottom-width: 8rpx; border-left-width: 8rpx; border-bottom-left-radius: 24rpx; }
    &--br { bottom: -2rpx; right: -2rpx; border-bottom-width: 8rpx; border-right-width: 8rpx; border-bottom-right-radius: 24rpx; }
  }

  &__icon {
    font-size: 96rpx;
    margin-bottom: 24rpx;
  }

  &__text {
    font-size: 28rpx;
    color: #999999;
  }
}

.manual-input {
  width: 100%;
  padding: 0 24rpx;

  &__label {
    font-size: 26rpx;
    color: #666666;
    margin-bottom: 16rpx;
    display: block;
  }

  &__row {
    display: flex;
    gap: 16rpx;
  }

  &__field {
    flex: 1;
    height: 80rpx;
    background-color: #ffffff;
    border-radius: 12rpx;
    padding: 0 24rpx;
    font-size: 28rpx;
    border: 1rpx solid #e0e0e0;
  }

  &__placeholder {
    color: #cccccc;
  }

  &__btn {
    height: 80rpx;
    padding: 0 32rpx;
    background-color: #2563EB;
    color: #ffffff;
    border-radius: 12rpx;
    font-size: 28rpx;
    line-height: 80rpx;
    border: none;
  }
}

.result-area {
  padding: 24rpx;
}

.result-header {
  background: linear-gradient(135deg, #2563EB, #3b82f6);
  border-radius: 20rpx;
  padding: 40rpx 32rpx;
  margin-bottom: 24rpx;
  text-align: center;

  &--inspector {
    background: linear-gradient(135deg, #e65100, #ff9800);
  }

  &__icon {
    font-size: 72rpx;
    margin-bottom: 16rpx;
  }

  &__title {
    font-size: 36rpx;
    font-weight: 700;
    color: #ffffff;
    display: block;
    margin-bottom: 8rpx;
  }

  &__sub {
    font-size: 24rpx;
    color: rgba(255, 255, 255, 0.7);
    display: block;
  }

  &__badge {
    display: inline-block;
    margin-top: 16rpx;
    padding: 6rpx 24rpx;
    background-color: rgba(255, 255, 255, 0.2);
    border-radius: 20rpx;

    &--inspector {
      background-color: rgba(255, 255, 255, 0.25);
    }

    &-text {
      font-size: 22rpx;
      color: #ffffff;
    }
  }
}

.info-card {
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 24rpx 32rpx;
  margin-bottom: 20rpx;

  &--inspector {
    border-left: 6rpx solid #ff9800;
  }

  &__title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333333;
    display: block;
    margin-bottom: 20rpx;
    padding-bottom: 16rpx;
    border-bottom: 1rpx solid #f0f0f0;
  }

  &__empty {
    padding: 24rpx 0;
    text-align: center;
  }

  &__empty-text {
    font-size: 26rpx;
    color: #cccccc;
  }
}

.info-row {
  display: flex;
  padding: 12rpx 0;

  &__label {
    width: 180rpx;
    font-size: 26rpx;
    color: #999999;
    flex-shrink: 0;
  }

  &__value {
    flex: 1;
    font-size: 26rpx;
    color: #333333;
  }
}

.appeal-item, .rect-item, .insp-item, .contact-item, .report-item {
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-child { border-bottom: none; }

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8rpx;
  }

  &__title, &__no, &__name {
    font-size: 28rpx;
    color: #333333;
    flex: 1;
  }

  &__time {
    font-size: 24rpx;
    color: #999999;
  }

  &__status, &__result {
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    font-size: 22rpx;
    margin-left: 12rpx;

    &--PENDING { background-color: rgba(255, 149, 0, 0.1); }
    &--ASSIGNED { background-color: rgba(26, 115, 232, 0.1); }
    &--HANDLING { background-color: rgba(26, 115, 232, 0.1); }
    &--HANDLED { background-color: rgba(52, 199, 89, 0.1); }
    &--CLOSED { background-color: rgba(153, 153, 153, 0.1); }
    &--IN_PROGRESS { background-color: rgba(26, 115, 232, 0.1); }
    &--SUBMITTED { background-color: rgba(52, 199, 89, 0.1); }
    &--ACCEPTED { background-color: rgba(52, 199, 89, 0.1); }
    &--REJECTED { background-color: rgba(255, 59, 48, 0.1); }
    &--QUALIFIED { background-color: rgba(52, 199, 89, 0.1); }
    &--UNQUALIFIED { background-color: rgba(255, 59, 48, 0.1); }
  }

  &__status-text, &__result {
    font-size: 22rpx;
  }
}

.contact-item {
  display: flex;
  align-items: center;
  justify-content: space-between;

  &__info {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  &__name {
    font-size: 28rpx;
    color: #333333;
    font-weight: 500;
  }

  &__position {
    font-size: 24rpx;
    color: #999999;
  }

  &__phone {
    font-size: 26rpx;
    color: #2563EB;
  }
}

.report-item {
  display: flex;
  align-items: center;
  justify-content: space-between;

  &__name {
    font-size: 28rpx;
    color: #333333;
    flex: 1;
  }

  &__status {
    font-size: 24rpx;
    color: #999999;
  }
}

.rescan-btn {
  padding: 24rpx 32rpx 48rpx;
}

.btn-rescan {
  width: 100%;
  height: 88rpx;
  background-color: #2563EB;
  color: #ffffff;
  border-radius: 44rpx;
  font-size: 30rpx;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
