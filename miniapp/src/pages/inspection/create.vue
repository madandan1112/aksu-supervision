<template>
  <view class="page-inspect">
    <view class="inspect-body">
      <!-- 企业选择 -->
      <view class="card">
        <view class="card__title-row">
          <text class="card__title">检查对象</text>
        </view>
        <view v-if="enterprise" class="ent">
          <text class="ent__name">{{ enterprise.enterpriseName || enterprise.name }}</text>
          <text class="ent__meta">{{ enterprise.creditCode || '' }} · {{ enterprise.industry || '' }}</text>
          <view class="ent__change" @tap="showSearch = true">
            <text class="ent__change-text">更换企业</text>
          </view>
        </view>
        <view v-else class="ent-empty" @tap="showSearch = true">
          <text class="ent-empty__text">＋ 点击选择 / 搜索检查企业</text>
        </view>
      </view>

      <!-- 检查类型 -->
      <view class="card">
        <view class="card__title-row">
          <text class="card__title">检查类型</text>
          <text class="card__hint">已选 {{ form.checkTypes.length }} 项</text>
        </view>
        <view class="types">
          <view
            v-for="t in checkTypeOptions"
            :key="t.code"
            class="types__item"
            :class="{ 'types__item--on': form.checkTypes.includes(t.code) }"
            @tap="toggleType(t.code)"
          >
            <text class="types__item-text">{{ t.name }}</text>
          </view>
        </view>
      </view>

      <!-- 问题登记 -->
      <view class="card card--danger">
        <view class="card__title-row">
          <text class="card__title">问题登记</text>
          <text class="card__hint">不合格项必填</text>
        </view>
        <view class="field">
          <text class="field__label">问题描述</text>
          <textarea
            class="field__textarea"
            v-model="form.issues"
            placeholder="如：后厨排水沟清洁不彻底；1 名员工健康证过期仍在岗"
            placeholder-class="field__placeholder"
            :maxlength="500"
          />
        </view>
        <view class="field">
          <text class="field__label">整改要求</text>
          <textarea
            class="field__textarea"
            v-model="form.requirements"
            placeholder="如：限期 3 日深度清洁复查 · 补办健康证"
            placeholder-class="field__placeholder"
            :maxlength="500"
          />
        </view>
        <view class="field">
          <text class="field__label">整改期限</text>
          <picker mode="date" :value="form.deadline" :start="today" @change="e => form.deadline = e.detail.value">
            <view class="field__picker">
              <text :class="form.deadline ? 'field__picker-text' : 'field__picker-text field__picker-text--ph'">
                {{ form.deadline || '请选择整改截止日期' }}
              </text>
            </view>
          </picker>
        </view>

        <!-- 证据照片 -->
        <view class="field">
          <text class="field__label">现场取证（时间/定位自动水印）</text>
          <view class="photos">
            <image
              v-for="(img, i) in photoPaths"
              :key="i"
              class="photos__item"
              :src="img"
              mode="aspectFill"
              @tap="previewPhoto(i)"
            />
            <view v-if="photoPaths.length < 9" class="photos__add" @tap="takePhoto">
              <text class="photos__add-text">📷</text>
            </view>
          </view>
        </view>
      </view>

      <button class="btn-submit" :loading="submitting" @tap="submitInspection">
        提交 · 自动生成整改通知书
      </button>
      <text class="page-foot">弱网自动保存草稿 · 提交即通知企业与预警系统</text>
    </view>

    <!-- 企业搜索弹层 -->
    <view v-if="showSearch" class="search-mask" @tap="showSearch = false">
      <view class="search-panel" @tap.stop>
        <view class="search-bar">
          <input
            class="search-bar__input"
            v-model="keyword"
            placeholder="输入企业名称 / 信用代码"
            placeholder-class="search-bar__ph"
            confirm-type="search"
            @confirm="searchEnterprise"
          />
          <view class="search-bar__btn" @tap="searchEnterprise">
            <text class="search-bar__btn-text">搜索</text>
          </view>
        </view>
        <scroll-view class="search-list" scroll-y>
          <view v-if="searchLoading" class="search-tip">
            <text class="search-tip__text">搜索中...</text>
          </view>
          <view v-else-if="results.length === 0" class="search-tip">
            <text class="search-tip__text">{{ hasSearched ? '未找到匹配企业' : '输入关键词搜索企业' }}</text>
          </view>
          <view v-for="item in results" :key="item.id" class="search-item" @tap="selectEnterprise(item)">
            <text class="search-item__name">{{ item.enterpriseName || item.name }}</text>
            <text class="search-item__meta">{{ item.creditCode || '' }} · {{ item.area || '' }}</text>
          </view>
        </scroll-view>
      </view>
    </view>

    <InspTabBar active="/pages/inspection/create" />
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get, post, uploadFile } from '@/utils/request'
import InspTabBar from '@/components/InspTabBar.vue'

const enterprise = ref(null)
const showSearch = ref(false)
const keyword = ref('')
const results = ref([])
const searchLoading = ref(false)
const hasSearched = ref(false)
const submitting = ref(false)
const photoPaths = ref([])
const photoUrls = ref([])

const today = new Date().toISOString().split('T')[0]

const checkTypeOptions = ref([
  { code: 'FOOD_PRODUCTION', name: '食品生产' },
  { code: 'FOOD_CIRCULATION', name: '食品流通' },
  { code: 'FOOD_SERVICE', name: '餐饮服务' },
  { code: 'DRUG_DEVICE', name: '药械' },
  { code: 'SPECIAL_EQUIPMENT', name: '特种设备' },
  { code: 'PRODUCT_QUALITY', name: '产品质量' }
])

const form = ref({
  enterpriseId: null,
  checkTypes: [],
  issues: '',
  requirements: '',
  deadline: '',
  remark: ''
})

onShow(() => {
  uni.hideTabBar({ animation: false }).catch?.(() => {})
  loadTypes()
})

async function loadTypes() {
  try {
    const data = await get('/inspector/inspection/types')
    const list = data?.list || data?.records || (Array.isArray(data) ? data : null)
    if (list && list.length) {
      checkTypeOptions.value = list.map(t => ({
        code: t.code || t.typeCode || t.name,
        name: t.name || t.typeName || t.code
      }))
    }
  } catch (e) { /* 使用默认类型 */ }
}

function toggleType(code) {
  const idx = form.value.checkTypes.indexOf(code)
  if (idx > -1) {
    form.value.checkTypes.splice(idx, 1)
  } else {
    form.value.checkTypes.push(code)
  }
}

async function searchEnterprise() {
  if (!keyword.value.trim()) return
  searchLoading.value = true
  hasSearched.value = true
  try {
    // 关键词搜索走企业列表接口（带数据权限：执法员仅见属地企业）；/enterprise/scan 仅支持码精确查询
    const data = await get('/inspector/enterprise/list', { keyword: keyword.value.trim(), page: 1, size: 20 })
    const list = data?.list || data?.content || data?.records || (Array.isArray(data) ? data : [])
    results.value = Array.isArray(list) ? list : []
  } catch (e) {
    results.value = []
  } finally {
    searchLoading.value = false
  }
}

function selectEnterprise(item) {
  enterprise.value = item
  form.value.enterpriseId = item.id
  showSearch.value = false
}

function takePhoto() {
  uni.chooseImage({
    count: 9 - photoPaths.value.length,
    sizeType: ['compressed'],
    success: (res) => {
      photoPaths.value.push(...res.tempFilePaths)
    }
  })
}

function previewPhoto(i) {
  uni.previewImage({ urls: photoPaths.value, current: i })
}

async function submitInspection() {
  if (!form.value.enterpriseId) {
    uni.showToast({ title: '请先选择检查企业', icon: 'none' })
    return
  }
  if (form.value.checkTypes.length === 0) {
    uni.showToast({ title: '请至少选择一项检查类型', icon: 'none' })
    return
  }
  if (!form.value.issues.trim()) {
    uni.showToast({ title: '请输入问题描述', icon: 'none' })
    return
  }
  if (!form.value.requirements.trim()) {
    uni.showToast({ title: '请输入整改要求', icon: 'none' })
    return
  }
  if (!form.value.deadline) {
    uni.showToast({ title: '请选择整改期限', icon: 'none' })
    return
  }

  submitting.value = true
  uni.showLoading({ title: '提交中...' })
  try {
    // 逐张上传现场照片
    const uploadedUrls = []
    for (const p of photoPaths.value) {
      try {
        const res = await uploadFile('/file/upload', p, 'file', { folder: 'inspection-images' })
        uploadedUrls.push(res.objectName || res.url || '')
      } catch (e) { /* 单张失败不阻断 */ }
    }

    await post('/inspector/inspection/submit', {
      enterpriseId: form.value.enterpriseId,
      // 后端契约：checkTypes 为逗号分隔字符串（非数组）
      checkTypes: form.value.checkTypes.join(','),
      issues: form.value.issues,
      requirements: form.value.requirements,
      deadline: form.value.deadline + 'T00:00:00',
      remark: form.value.remark,
      evidenceImages: JSON.stringify(uploadedUrls.filter(Boolean)),
      evidenceVideos: JSON.stringify([]),
      attachmentUrls: JSON.stringify([])
    })
    uni.hideLoading()
    uni.showToast({ title: '整改通知已下发', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 1200)
  } catch (e) {
    uni.hideLoading()
    uni.showToast({ title: e.message || '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.page-inspect {
  min-height: 100vh;
  background-color: #f6f7f9;
  padding-bottom: 200rpx;
}

.inspect-body {
  padding: 24rpx 28rpx 0;
}

.card {
  background-color: #ffffff;
  border-radius: 28rpx;
  border: 1rpx solid #ececf0;
  padding: 28rpx;
  margin-bottom: 24rpx;

  &--danger {
    border-color: #f4cfd6;
    background-color: #fffafa;
  }

  &__title-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 22rpx;
  }

  &__title {
    font-size: 30rpx;
    font-weight: 700;
    color: #18181b;
  }

  &__hint {
    font-size: 22rpx;
    color: #a1a1aa;
  }
}

.ent {
  &__name {
    display: block;
    font-size: 32rpx;
    font-weight: 600;
    color: #18181b;
  }

  &__meta {
    display: block;
    font-size: 22rpx;
    color: #a1a1aa;
    margin-top: 8rpx;
  }

  &__change {
    margin-top: 16rpx;
    align-self: flex-start;
    background-color: #f4f4f5;
    border-radius: 999rpx;
    padding: 8rpx 24rpx;
  }

  &__change-text {
    font-size: 22rpx;
    color: #52525b;
  }
}

.ent-empty {
  border: 3rpx dashed #d4d4d8;
  border-radius: 20rpx;
  padding: 40rpx 0;
  display: flex;
  align-items: center;
  justify-content: center;

  &__text {
    font-size: 26rpx;
    color: #c8102e;
  }
}

.types {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;

  &__item {
    border-radius: 999rpx;
    border: 2rpx solid #e4e4e7;
    background-color: #ffffff;
    padding: 12rpx 30rpx;

    &--on {
      background: linear-gradient(135deg, #d5263d, #b00e24);
      border-color: #b00e24;
    }
  }

  &__item-text {
    font-size: 24rpx;
    color: #52525b;

    .types__item--on & {
      color: #ffffff;
      font-weight: 600;
    }
  }
}

.field {
  margin-bottom: 26rpx;

  &__label {
    display: block;
    font-size: 24rpx;
    font-weight: 600;
    color: #52525b;
    margin-bottom: 12rpx;
  }

  &__textarea {
    width: 100%;
    min-height: 140rpx;
    background-color: #ffffff;
    border: 2rpx solid #e4e4e7;
    border-radius: 20rpx;
    padding: 20rpx;
    font-size: 26rpx;
    color: #18181b;
    box-sizing: border-box;
  }

  &__placeholder {
    color: #c8c8cd;
  }

  &__picker {
    background-color: #ffffff;
    border: 2rpx solid #e4e4e7;
    border-radius: 20rpx;
    padding: 22rpx 20rpx;
  }

  &__picker-text {
    font-size: 26rpx;
    color: #18181b;

    &--ph {
      color: #c8c8cd;
    }
  }
}

.photos {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;

  &__item {
    width: 150rpx;
    height: 150rpx;
    border-radius: 20rpx;
  }

  &__add {
    width: 150rpx;
    height: 150rpx;
    border-radius: 20rpx;
    border: 3rpx dashed #f4cfd6;
    background-color: #ffffff;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__add-text {
    font-size: 44rpx;
    color: #c8102e;
  }
}

.btn-submit {
  width: 100%;
  height: 104rpx;
  background: linear-gradient(135deg, #d5263d, #b00e24);
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 600;
  box-shadow: 0 10rpx 28rpx rgba(200, 16, 46, 0.28);
}

.page-foot {
  display: block;
  text-align: center;
  font-size: 22rpx;
  color: #a1a1aa;
  margin-top: 20rpx;
}

/* 搜索弹层 */
.search-mask {
  position: fixed;
  inset: 0;
  background-color: rgba(24, 24, 27, 0.45);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
}

.search-panel {
  width: 100%;
  background-color: #ffffff;
  border-radius: 44rpx 44rpx 0 0;
  padding: 32rpx 28rpx 48rpx;
  max-height: 70vh;
  display: flex;
  flex-direction: column;
}

.search-bar {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;

  &__input {
    flex: 1;
    height: 88rpx;
    background-color: #f4f4f5;
    border-radius: 22rpx;
    padding: 0 28rpx;
    font-size: 28rpx;
    color: #18181b;
  }

  &__ph {
    color: #a1a1aa;
  }

  &__btn {
    width: 140rpx;
    height: 88rpx;
    border-radius: 22rpx;
    background: linear-gradient(135deg, #d5263d, #b00e24);
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__btn-text {
    font-size: 26rpx;
    color: #ffffff;
    font-weight: 600;
  }
}

.search-list {
  max-height: 50vh;
}

.search-tip {
  padding: 60rpx 0;
  display: flex;
  justify-content: center;

  &__text {
    font-size: 26rpx;
    color: #a1a1aa;
  }
}

.search-item {
  padding: 26rpx 12rpx;
  border-bottom: 1rpx solid #f4f4f5;

  &__name {
    display: block;
    font-size: 28rpx;
    font-weight: 600;
    color: #18181b;
  }

  &__meta {
    display: block;
    font-size: 22rpx;
    color: #a1a1aa;
    margin-top: 6rpx;
  }
}
</style>
